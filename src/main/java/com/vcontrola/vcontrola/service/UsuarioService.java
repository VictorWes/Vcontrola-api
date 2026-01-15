package com.vcontrola.vcontrola.service;

import com.vcontrola.vcontrola.controller.request.UsuarioRequest;
import com.vcontrola.vcontrola.controller.response.UsuarioResponse;
import com.vcontrola.vcontrola.entity.Usuario;
import com.vcontrola.vcontrola.mapper.UsuarioMapper;
import com.vcontrola.vcontrola.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.Data;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;


    public UsuarioService(UsuarioRepository usuarioRepository, UsuarioMapper usuarioMapper) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
    }

    @Transactional
    public UsuarioResponse cadastrarUsuario(UsuarioRequest request) {

        if (usuarioRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Já existe um usuário com este e-mail.");
        }
        Usuario usuarioNovo = usuarioMapper.toEntity(request);
        Usuario usuarioSalvo = usuarioRepository.save(usuarioNovo);

        return usuarioMapper.toResponse(usuarioSalvo);
    }
}
