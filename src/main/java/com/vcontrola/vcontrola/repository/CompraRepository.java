package com.vcontrola.vcontrola.repository;

import com.vcontrola.vcontrola.entity.Compra;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CompraRepository extends JpaRepository<Compra, UUID> {
    List<Compra> findByCartaoIdOrderByDataCompraDesc(UUID cartaoId);
    Page<Compra> findByCartaoId(UUID cartaoId, Pageable pageable);
}
