package com.vcontrola.vcontrola.mapper;

import com.vcontrola.vcontrola.controller.request.UsuarioRequest;
import com.vcontrola.vcontrola.controller.response.UsuarioResponse;
import com.vcontrola.vcontrola.entity.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public Usuario toEntity(UsuarioRequest request) {
        if (request == null) {
            return null;
        }

        Usuario usuario = new Usuario();
        usuario.setNome(request.nome());
        usuario.setEmail(request.email());
        usuario.setSenha(request.senha());

        return usuario;
    }

    public UsuarioResponse toResponse(Usuario usuario) {
        if (usuario == null) {
            return null;
        }
        boolean isGoogle = usuario.getSenha() == null;

        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                isGoogle
        );
    }
}
