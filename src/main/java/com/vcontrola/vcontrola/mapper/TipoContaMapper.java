package com.vcontrola.vcontrola.mapper;

import com.vcontrola.vcontrola.controller.request.TipoContaRequest;
import com.vcontrola.vcontrola.controller.response.TipoContaResponse;
import com.vcontrola.vcontrola.entity.TipoContaUsuario;
import com.vcontrola.vcontrola.entity.Usuario;
import com.vcontrola.vcontrola.enums.TipoConta;
import org.springframework.stereotype.Component;

@Component
public class TipoContaMapper {
    public TipoContaUsuario toEntity(TipoContaRequest request, Usuario usuario) {
        TipoContaUsuario tipo = new TipoContaUsuario();

        tipo.setNome(request.nome());
        tipo.setUsuario(usuario);


        tipo.setIcone(request.icone() != null && !request.icone().isBlank()
                ? request.icone()
                : "account_balance_wallet");

        tipo.setComportamento(request.comportamento() != null
                ? request.comportamento()
                : TipoConta.CONTA_CORRENTE);

        return tipo;
    }

    public TipoContaResponse toResponse(TipoContaUsuario entity) {
        return new TipoContaResponse(
                entity.getId(),
                entity.getNome(),
                entity.getIcone(),
                entity.getComportamento()
        );
    }
}
