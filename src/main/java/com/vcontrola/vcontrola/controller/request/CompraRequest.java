package com.vcontrola.vcontrola.controller.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CompraRequest(
        @NotBlank(message = "O nome da compra é obrigatório")
        String nome,

        @NotNull(message = "O valor total é obrigatório")
        @Positive(message = "O valor deve ser positivo")
        BigDecimal valorTotal,

        @NotNull(message = "A quantidade de parcelas é obrigatória")
        @Min(value = 1, message = "Mínimo de 1 parcela")
        Integer qtdeParcelas,

        @NotNull(message = "A data da compra é obrigatória")
        LocalDate dataCompra,

        @NotNull(message = "O ID do cartão é obrigatório")
        UUID cartaoId
) {
}
