package com.vcontrola.vcontrola.repository;

import com.vcontrola.vcontrola.entity.CartaoCredito;
import com.vcontrola.vcontrola.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CartaoCreditoRepository extends JpaRepository<CartaoCredito, UUID> {
    List<CartaoCredito> findByUsuarioId(UUID usuarioId);
    List<CartaoCredito> findByUsuario(Usuario usuario);
}
