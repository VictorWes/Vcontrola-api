package com.vcontrola.vcontrola.repository;

import com.vcontrola.vcontrola.entity.Parcela;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface ParcelaRepository extends JpaRepository<Parcela, UUID> {
    List<Parcela> findByCompraIdOrderByNumeroParcelaAsc(UUID compraId);

    @Query("""
        SELECT COALESCE(SUM(p.valorParcela), 0) 
        FROM Parcela p 
        JOIN p.compra c 
        WHERE c.cartao.id = :cartaoId 
        AND p.paga = false 
        AND p.dataVencimento BETWEEN :inicio AND :fim
    """)
    BigDecimal somarFaturaAtual(
            @Param("cartaoId") UUID cartaoId,
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim
    );

    boolean existsByCompraIdAndPagaFalse(UUID compraId);
}
