package com.vcontrola.vcontrola.service;

import com.vcontrola.vcontrola.controller.request.AlterarSenhaRequest;
import com.vcontrola.vcontrola.controller.request.AtualizarUsuarioRequest;
import com.vcontrola.vcontrola.controller.request.LoginRequest;
import com.vcontrola.vcontrola.controller.request.UsuarioRequest;
import com.vcontrola.vcontrola.controller.response.LoginResponse;
import com.vcontrola.vcontrola.controller.response.UsuarioResponse;
import com.vcontrola.vcontrola.entity.TipoContaUsuario;
import com.vcontrola.vcontrola.entity.Usuario;
import com.vcontrola.vcontrola.enums.TipoConta;
import com.vcontrola.vcontrola.infra.exception.RegraDeNegocioException;
import com.vcontrola.vcontrola.mapper.UsuarioMapper;
import com.vcontrola.vcontrola.repository.TipoContaUsuarioRepository;
import com.vcontrola.vcontrola.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final TipoContaUsuarioRepository tipoContaUsuarioRepository;
    @Autowired
    private EmailService emailService;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    public UsuarioService(UsuarioRepository usuarioRepository, UsuarioMapper usuarioMapper, PasswordEncoder passwordEncoder, TokenService tokenService, TipoContaUsuarioRepository tipoContaUsuarioRepository) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.tipoContaUsuarioRepository = tipoContaUsuarioRepository;
    }

    @Transactional
    public UsuarioResponse cadastrarUsuario(UsuarioRequest request) {


        if (usuarioRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Já existe um usuário com este e-mail.");
        }

        Usuario usuarioNovo = usuarioMapper.toEntity(request);
        String senhaCriptografada = passwordEncoder.encode(usuarioNovo.getSenha());
        usuarioNovo.setSenha(senhaCriptografada);
        Usuario usuarioSalvo = usuarioRepository.save(usuarioNovo);
        inicializarTiposPadrao(usuarioSalvo);


        try {
            String mensagem = String.format("""
                Olá, %s!
                
                Seja muito bem-vindo(a) ao VControla! 🚀
                
                Seu cadastro foi realizado com sucesso. Agora você tem o controle total 
                da sua vida financeira na palma da mão.
                
                Acesse agora: https://vcontrola.vercel.app/auth/login
                
                Atenciosamente,
                Equipe VControla.
                """, usuarioSalvo.getNome());

            emailService.enviarEmailTexto(
                    usuarioSalvo.getEmail(),
                    "Bem-vindo ao VControla! 🎉",
                    mensagem
            );
        } catch (Exception e) {
            System.err.println("Falha ao enviar e-mail de boas-vindas: " + e.getMessage());
        }
        // 👆 FIM DO BLOCO DE E-MAIL 👆

        return usuarioMapper.toResponse(usuarioSalvo);
    }

    public LoginResponse autenticar(LoginRequest request) {

        Usuario usuario = usuarioRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Usuário ou senha inválidos"));


        if (!passwordEncoder.matches(request.senha(), usuario.getSenha())) {
            throw new RuntimeException("Usuário ou senha inválidos");
        }

        String token = tokenService.gerarToken(usuario);
        return new LoginResponse(token, usuario.getNome());
    }

    private void inicializarTiposPadrao(Usuario usuario) {
        criarTipo(usuario, "Conta Corrente", "account_balance", TipoConta.CONTA_CORRENTE);
        criarTipo(usuario, "Dinheiro / Carteira", "wallet", TipoConta.ESPECIE);
        criarTipo(usuario, "Cartão de Crédito", "credit_card", TipoConta.CREDITO);
        criarTipo(usuario, "Poupança / Reserva", "savings", TipoConta.CONTA_CORRENTE);
    }

    private void criarTipo(Usuario usuario, String nome, String icone, TipoConta comportamento) {
        TipoContaUsuario tipo = new TipoContaUsuario();
        tipo.setNome(nome);
        tipo.setIcone(icone);
        tipo.setComportamento(comportamento);
        tipo.setUsuario(usuario);

        tipoContaUsuarioRepository.save(tipo);
    }

    @Transactional
    public void alterarSenha(AlterarSenhaRequest dados, Usuario usuario) {
        if (!passwordEncoder.matches(dados.senhaAtual(), usuario.getSenha())) {
            throw new RegraDeNegocioException("A senha atual informada está incorreta.");
        }

        String novaSenhaHash = passwordEncoder.encode(dados.novaSenha());
        usuario.setSenha(novaSenhaHash);

        usuarioRepository.save(usuario);
    }

    @Transactional
    public UsuarioResponse atualizarPerfil(Usuario usuarioDoToken, AtualizarUsuarioRequest dados) {


        Usuario usuarioBanco = usuarioRepository.findById(usuarioDoToken.getId())
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
        usuarioBanco.setNome(dados.nome());
        if (usuarioBanco.getSenha() != null && dados.email() != null) {
            if (!dados.email().equals(usuarioBanco.getEmail())) {
                boolean emailExiste = usuarioRepository.findByEmail(dados.email()).isPresent();
                if (emailExiste) {
                    throw new RegraDeNegocioException("Este e-mail já está em uso.");
                }
                usuarioBanco.setEmail(dados.email());
            }
        }
        Usuario usuarioSalvo = usuarioRepository.save(usuarioBanco);
        return usuarioMapper.toResponse(usuarioSalvo);
    }

    public void recuperarSenha(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        String token = UUID.randomUUID().toString();

        usuario.setTokenRecuperacao(token);
        usuarioRepository.save(usuario);

        String link = frontendUrl + "/conta/nova-senha?token=" + token;

        String mensagem = String.format("""
                Olá, %s!
                Clique abaixo para redefinir sua senha:
                %s
                """, usuario.getNome(), link);

        emailService.enviarEmailTexto(usuario.getEmail(), "Recuperação de Senha", mensagem);
    }

    @Transactional
    public void redefinirSenha(String token, String novaSenha) {
        Usuario usuario = usuarioRepository.findByTokenRecuperacao(token)
                .orElseThrow(() -> new RegraDeNegocioException("Link inválido ou expirado."));

        usuario.setSenha(passwordEncoder.encode(novaSenha));

        usuario.setTokenRecuperacao(null);

        usuarioRepository.save(usuario);
    }
}
