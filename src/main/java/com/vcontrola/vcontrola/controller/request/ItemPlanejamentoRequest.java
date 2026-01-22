package com.vcontrola.vcontrola.controller.request;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record ItemPlanejamentoRequest (
        @NotNull UUID carteiraId,
        @NotNull BigDecimal valor,
        @NotNull UUID contaDestinoId
) {
}
