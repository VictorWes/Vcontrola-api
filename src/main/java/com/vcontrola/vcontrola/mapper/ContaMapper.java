package com.vcontrola.vcontrola.mapper;

import com.vcontrola.vcontrola.controller.request.ContaRequest;
import com.vcontrola.vcontrola.controller.response.ContaResponse;
import com.vcontrola.vcontrola.entity.Conta;
import com.vcontrola.vcontrola.entity.TipoContaUsuario;
import com.vcontrola.vcontrola.entity.Usuario;
import com.vcontrola.vcontrola.enums.TipoConta;
import com.vcontrola.vcontrola.repository.TipoContaUsuarioRepository;
import org.springframework.stereotype.Component;

@Component
public class ContaMapper {

    private final TipoContaUsuarioRepository tipoRepo;

    // constructor injection to allow proper DI and avoid static-analysis warnings
    public ContaMapper(TipoContaUsuarioRepository tipoRepo) {
        this.tipoRepo = tipoRepo;
    }

    public Conta toEntity(ContaRequest request, Usuario usuario) {
        Conta conta = new Conta();
        conta.setNome(request.nome());
        conta.setSaldo(request.saldo());
        // define o tipo da conta a partir do enum e do usuário
        conta.setTipo(mapTipo(request.tipo(), usuario));
        conta.setUsuario(usuario);
        return conta;
    }

    public TipoContaUsuario mapTipo(TipoConta tipo, Usuario usuario) {
        return tipoRepo.findByUsuarioAndComportamento(usuario, tipo)
                .orElseThrow(() -> new RuntimeException("Tipo de conta '" + tipo + "' não encontrado para o usuário."));
    }

    public ContaResponse toResponse(Conta conta) {
        return new ContaResponse(
                conta.getId(),
                conta.getNome(),
                conta.getSaldo(),
                // retorna o enum comportamento da entidade TipoContaUsuario
                conta.getTipo() != null ? conta.getTipo().getComportamento() : null
        );
    }
}
