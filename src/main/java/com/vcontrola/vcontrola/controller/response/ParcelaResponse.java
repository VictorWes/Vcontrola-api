package com.vcontrola.vcontrola.controller.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ParcelaResponse(
        UUID id,
        Integer numeroParcela,
        BigDecimal valorParcela,
        LocalDate dataVencimento,
        boolean paga
) {
}
