package com.vcontrola.vcontrola.controller;

import com.vcontrola.vcontrola.controller.request.TipoContaRequest;
import com.vcontrola.vcontrola.controller.response.TipoContaResponse;
import com.vcontrola.vcontrola.entity.TipoContaUsuario;
import com.vcontrola.vcontrola.entity.Usuario;
import com.vcontrola.vcontrola.mapper.TipoContaMapper;
import com.vcontrola.vcontrola.repository.TipoContaUsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tipos-conta")
public class TipoContaController {
    @Autowired
    private TipoContaUsuarioRepository repository;


    @Autowired
    private TipoContaMapper mapper;

    @GetMapping
    public ResponseEntity<List<TipoContaResponse>> listar(Authentication auth) {
        Usuario usuario = (Usuario) auth.getPrincipal(); // Pega o usuário logado


        List<TipoContaUsuario> entidades = repository.findByUsuario(usuario);

        var responses = entidades.stream()
                .map(mapper::toResponse)
                .toList();

        return ResponseEntity.ok(responses);
    }

    @PostMapping
    public ResponseEntity<Void> criar(@RequestBody @Valid TipoContaRequest request, Authentication auth) {
        Usuario usuario = (Usuario) auth.getPrincipal();


        TipoContaUsuario novoTipo = mapper.toEntity(request, usuario);


        repository.save(novoTipo);

        return ResponseEntity.ok().build();
    }
}
