package com.vcontrola.vcontrola.repository;

import com.vcontrola.vcontrola.entity.Transacao;
import com.vcontrola.vcontrola.enums.StatusTransacaoCartao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, UUID> {
    // Busca transações de uma conta específica dentro de um mês
    List<Transacao> findByContaIdAndDataBetween(UUID contaId, LocalDate inicio, LocalDate fim);

    // Busca todas as transações de um grupo (parcelas de uma mesma compra)
    List<Transacao> findByTransactionGroupId(UUID transactionGroupId);

    // Busca transações por status (ex: tudo que ainda está PENDENTE)
    List<Transacao> findByStatus(StatusTransacaoCartao status);

    List<Transacao> findByContaIdOrderByDataDesc(UUID contaId);

    @Query("SELECT t FROM Transacao t WHERE t.conta.usuario.id = :usuarioId ORDER BY t.data DESC")
    List<Transacao> findAllByUsuarioId(UUID usuarioId);
}
