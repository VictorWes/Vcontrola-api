package com.vcontrola.vcontrola.controller.response;

import com.vcontrola.vcontrola.enums.StatusTransacaoCartao;
import com.vcontrola.vcontrola.enums.TipoTransacao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record TransacaoResponse (
        UUID id,
        String descricao,
        BigDecimal valor,
        TipoTransacao tipo,
        StatusTransacaoCartao status,
        LocalDate data,
        UUID contaId,
        String contaNome,
        String numeroParcela
) {
}
