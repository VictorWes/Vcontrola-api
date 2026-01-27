package com.vcontrola.vcontrola.controller.response;

import java.math.BigDecimal;
import java.util.UUID;

public record CartaoResponse(
        UUID id,
        String nome,
        BigDecimal limite,
        Integer diaVencimento,
        Integer diaFechamento,
        BigDecimal valorFaturaAtual
) {
}
