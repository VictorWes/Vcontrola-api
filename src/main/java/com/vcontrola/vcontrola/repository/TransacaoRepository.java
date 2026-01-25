package com.vcontrola.vcontrola.repository;

import com.vcontrola.vcontrola.entity.Transacao;
import com.vcontrola.vcontrola.entity.Usuario;
import com.vcontrola.vcontrola.enums.StatusTransacaoCartao;
import com.vcontrola.vcontrola.enums.TipoTransacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
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

    @Query("SELECT COALESCE(SUM(t.valor), 0) FROM Transacao t " +
            "WHERE t.conta.usuario.id = :usuarioId " +
            "AND t.tipo = :tipo " +
            "AND t.data BETWEEN :inicio AND :fim")
    BigDecimal somarPorTipo(@Param("usuarioId") UUID usuarioId,
                            @Param("tipo") TipoTransacao tipo,
                            @Param("inicio") LocalDate inicio,
                            @Param("fim") LocalDate fim);
}
