package com.vcontrola.vcontrola.repository;

import com.vcontrola.vcontrola.entity.CartaoCredito;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CartaoCreditoRepository {
    List<CartaoCredito> findByUsuarioId(UUID usuarioId);
}
