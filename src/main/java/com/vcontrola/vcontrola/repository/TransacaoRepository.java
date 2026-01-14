package com.vcontrola.vcontrola.repository;

import com.vcontrola.vcontrola.entity.Transacao;
import com.vcontrola.vcontrola.enums.StatusTransacaoCartao;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface TransacaoRepository {
    // Busca transações de uma conta específica dentro de um mês
    List<Transacao> findByContaIdAndDataBetween(UUID contaId, LocalDate inicio, LocalDate fim);

    // Busca todas as transações de um grupo (parcelas de uma mesma compra)
    List<Transacao> findByTransactionGroupId(UUID transactionGroupId);

    // Busca transações por status (ex: tudo que ainda está PENDENTE)
    List<Transacao> findByStatus(StatusTransacaoCartao status);
}
