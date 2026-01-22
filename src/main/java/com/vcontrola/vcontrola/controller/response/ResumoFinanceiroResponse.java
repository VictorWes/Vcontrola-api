package com.vcontrola.vcontrola.controller.response;

import java.math.BigDecimal;
import java.util.List;

public record ResumoFinanceiroResponse(
        BigDecimal saldoDisponivelVirtual,
        List<ItemPlanejamentoResponse> itens
) {
}
