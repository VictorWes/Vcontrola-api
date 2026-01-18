package com.vcontrola.vcontrola.controller.request;

import com.vcontrola.vcontrola.enums.StatusTransacaoCartao;
import com.vcontrola.vcontrola.enums.TipoTransacao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record TransacaoRequest (
        @NotBlank(message = "A descrição é obrigatória")
        String descricao,

        @NotNull(message = "O valor é obrigatório")
        @Positive(message = "O valor deve ser maior que zero")
        BigDecimal valor,

        @NotNull(message = "O tipo é obrigatório")
        TipoTransacao tipo,

        @NotNull(message = "O status é obrigatório")
        StatusTransacaoCartao status,

        @NotNull(message = "A data é obrigatória")
        LocalDate data,

        @NotNull(message = "A conta é obrigatória")
        UUID contaId,

        UUID cartaoId,

        String numeroParcela
) {
}
