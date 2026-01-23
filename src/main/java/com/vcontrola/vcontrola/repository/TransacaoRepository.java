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

    List<Transacao> findByContaIdAndDataBetween(UUID contaId, LocalDate inicio, LocalDate fim);

    List<Transacao> findByTransactionGroupId(UUID transactionGroupId);

    List<Transacao> findByStatus(StatusTransacaoCartao status);

    List<Transacao> findByContaIdOrderByDataDesc(UUID contaId);

    @Query("SELECT t FROM Transacao t WHERE t.conta.usuario.id = :usuarioId ORDER BY t.data DESC")
    List<Transacao> findAllByUsuarioId(UUID usuarioId);

    boolean existsByContaId(UUID contaId);
}
