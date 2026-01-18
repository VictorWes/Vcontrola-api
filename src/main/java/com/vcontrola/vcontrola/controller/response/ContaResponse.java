package com.vcontrola.vcontrola.controller.response;

import com.vcontrola.vcontrola.enums.TipoConta;

import java.math.BigDecimal;
import java.util.UUID;

public record ContaResponse(
        UUID id,
        String nome,
        BigDecimal saldo,
        TipoConta tipo
) {

}
