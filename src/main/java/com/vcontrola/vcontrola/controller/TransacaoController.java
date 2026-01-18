package com.vcontrola.vcontrola.controller;

import com.vcontrola.vcontrola.controller.request.TransacaoRequest;
import com.vcontrola.vcontrola.controller.response.TransacaoResponse;
import com.vcontrola.vcontrola.entity.Usuario;
import com.vcontrola.vcontrola.service.TransacaoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transacoes")
public class TransacaoController {

    @Autowired
    private TransacaoService service;

    @PostMapping
    public ResponseEntity<Void> criar(@RequestBody @Valid TransacaoRequest dados, Authentication auth) {
        Usuario usuario = (Usuario) auth.getPrincipal();

        service.criar(dados, usuario);

        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<TransacaoResponse>> listar(Authentication auth) {
        Usuario usuario = (Usuario) auth.getPrincipal();

        var lista = service.listar(usuario);

        return ResponseEntity.ok(lista);
    }
}
