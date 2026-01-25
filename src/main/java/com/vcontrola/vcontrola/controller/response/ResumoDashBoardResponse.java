package com.vcontrola.vcontrola.controller.response;

import java.math.BigDecimal;

public record ResumoDashBoardResponse(
        BigDecimal receitas,
        BigDecimal despesas,
        BigDecimal saldo
) {
}
