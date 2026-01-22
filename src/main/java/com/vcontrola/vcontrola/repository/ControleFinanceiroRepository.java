package com.vcontrola.vcontrola.repository;

import com.vcontrola.vcontrola.entity.ControleFinanceiro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ControleFinanceiroRepository extends JpaRepository<ControleFinanceiro, UUID> {
    Optional<ControleFinanceiro> findByUsuarioId(UUID usuarioId);
}
