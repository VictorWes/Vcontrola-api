package com.vcontrola.vcontrola.controller.request;


import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record SaldoVirtualRequest(
        @NotNull BigDecimal valor
) {
}
