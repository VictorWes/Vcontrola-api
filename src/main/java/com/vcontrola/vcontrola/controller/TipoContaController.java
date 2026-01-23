package com.vcontrola.vcontrola.controller;

import com.vcontrola.vcontrola.controller.request.AtualizarCarteiraRequest;
import com.vcontrola.vcontrola.controller.request.TipoContaRequest;
import com.vcontrola.vcontrola.controller.response.TipoContaResponse;
import com.vcontrola.vcontrola.entity.TipoContaUsuario;
import com.vcontrola.vcontrola.entity.Usuario;
import com.vcontrola.vcontrola.mapper.TipoContaMapper;
import com.vcontrola.vcontrola.repository.TipoContaUsuarioRepository;
import com.vcontrola.vcontrola.service.TipoContaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tipos-conta")
public class TipoContaController {
    @Autowired
    private TipoContaService service;

    @GetMapping
    public ResponseEntity<List<TipoContaResponse>> listar(Authentication auth) {
        Usuario usuario = (Usuario) auth.getPrincipal();
        var responses = service.listar(usuario);
        return ResponseEntity.ok(responses);
    }

    @PostMapping
    public ResponseEntity<Void> criar(@RequestBody @Valid TipoContaRequest request, Authentication auth) {
        Usuario usuario = (Usuario) auth.getPrincipal();
        service.criar(request, usuario);
        return ResponseEntity.ok().build();
    }




    @PutMapping("/{id}")
    public ResponseEntity<Void> atualizar(@PathVariable UUID id, @RequestBody AtualizarCarteiraRequest request, Authentication auth) {
        Usuario usuario = (Usuario) auth.getPrincipal();
        service.atualizar(id, request.nome(), usuario);
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable UUID id, Authentication auth) {
        Usuario usuario = (Usuario) auth.getPrincipal();
        service.excluir(id, usuario);
        return ResponseEntity.noContent().build();
    }
}
