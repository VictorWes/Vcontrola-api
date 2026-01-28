package com.vcontrola.vcontrola.controller.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CompraResponse (
        UUID id,
        String nome,
        BigDecimal valorTotal,
        Integer qtdeParcelas,
        BigDecimal valorParcela,
        LocalDate dataCompra
) {
}
