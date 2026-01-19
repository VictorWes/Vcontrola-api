package com.vcontrola.vcontrola.controller.response;

import com.vcontrola.vcontrola.enums.TipoConta;

import java.util.UUID;

public record TipoContaResponse(
        UUID id,
        String nome,
        String icone,
        TipoConta comportamento
) {
}
