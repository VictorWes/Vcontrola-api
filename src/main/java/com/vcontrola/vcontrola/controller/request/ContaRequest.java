package com.vcontrola.vcontrola.controller.request;

import com.vcontrola.vcontrola.enums.TipoConta;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record ContaRequest(
        @NotBlank(message = "O nome é obrigatório")
        String nome,

        @NotNull(message = "O saldo inicial é obrigatório")
        BigDecimal saldo,

        @NotNull
        UUID tipoId
) {
}
