package com.vcontrola.vcontrola.controller;

import com.vcontrola.vcontrola.controller.request.*;
import com.vcontrola.vcontrola.controller.response.LoginResponse;
import com.vcontrola.vcontrola.controller.response.UsuarioResponse;
import com.vcontrola.vcontrola.entity.Usuario;
import com.vcontrola.vcontrola.mapper.UsuarioMapper;
import com.vcontrola.vcontrola.service.GoogleLogService;
import com.vcontrola.vcontrola.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin(origins = "http://localhost:4200")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final GoogleLogService googleLogService;
    private final UsuarioMapper usuarioMapper;

    public UsuarioController(UsuarioService usuarioService, GoogleLogService googleLogService, UsuarioMapper usuarioMapper) {
        this.usuarioService = usuarioService;
        this.googleLogService = googleLogService;
        this.usuarioMapper = usuarioMapper;
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> cadastrar(@RequestBody @Valid UsuarioRequest request) {

        UsuarioResponse novoUsuario = usuarioService.cadastrarUsuario(request);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(novoUsuario.id())
                .toUri();

        return ResponseEntity.created(uri).body(novoUsuario);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {

        LoginResponse response = usuarioService.autenticar(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/google")
    public ResponseEntity loginGoogle(@RequestBody GoogleLoginRequest request) {
        try {
            LoginResponse response = googleLogService.autenticarGoogle(request.token());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @PutMapping("/perfil") 
    public ResponseEntity<UsuarioResponse> atualizarPerfil(
            @RequestBody @Valid AtualizarUsuarioRequest dados,
            @AuthenticationPrincipal Usuario usuarioLogado) {

        UsuarioResponse usuarioAtualizado = usuarioService.atualizarPerfil(usuarioLogado, dados);

        return ResponseEntity.ok(usuarioAtualizado);
    }

    @PatchMapping("/senha")
    public ResponseEntity<Void> alterarSenha(
            @RequestBody AlterarSenhaRequest dados,
            @AuthenticationPrincipal Usuario usuarioLogado) {

        usuarioService.alterarSenha(dados, usuarioLogado);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponse> buscarPerfil(@AuthenticationPrincipal Usuario usuarioLogado) {

        UsuarioResponse response = usuarioMapper.toResponse(usuarioLogado);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/conta/recuperar-senha")
    public ResponseEntity<Void> recuperarSenha(@RequestBody @Valid EsqueciSenhaRequest request) {
        usuarioService.recuperarSenha(request.email());

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/conta/redefinir-senha")
    public ResponseEntity<Void> redefinirSenha(@RequestBody @Valid DefinirNovaSenhaRequest request) {
        usuarioService.redefinirSenha(request.token(), request.novaSenha());

        return ResponseEntity.noContent().build();
    }


}
