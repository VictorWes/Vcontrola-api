package com.vcontrola.vcontrola.controller.response;


import com.vcontrola.vcontrola.enums.StatusPlanejamento;

import java.math.BigDecimal;
import java.util.UUID;

public record ItemPlanejamentoResponse(
        UUID id,
        String nomeCarteira,
        UUID carteiraId,
        BigDecimal valor,
        StatusPlanejamento status,
        String nomeContaDestino,
        UUID contaDestinoId
) {
}
