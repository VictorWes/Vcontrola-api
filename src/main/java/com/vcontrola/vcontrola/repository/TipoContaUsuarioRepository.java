package com.vcontrola.vcontrola.repository;

import com.vcontrola.vcontrola.entity.TipoContaUsuario;
import com.vcontrola.vcontrola.entity.Usuario;
import com.vcontrola.vcontrola.enums.TipoConta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TipoContaUsuarioRepository  extends JpaRepository<TipoContaUsuario, UUID> {
    List<TipoContaUsuario> findByUsuario(Usuario usuario);

    Optional<TipoContaUsuario> findByUsuarioAndComportamento(Usuario usuario, TipoConta comportamento);
}
