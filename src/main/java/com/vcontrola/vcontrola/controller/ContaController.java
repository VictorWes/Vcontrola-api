package com.vcontrola.vcontrola.controller;

import com.vcontrola.vcontrola.controller.request.ContaRequest;
import com.vcontrola.vcontrola.controller.response.ContaResponse;
import com.vcontrola.vcontrola.entity.Conta;
import com.vcontrola.vcontrola.entity.Usuario;
import com.vcontrola.vcontrola.mapper.ContaMapper;
import com.vcontrola.vcontrola.repository.ContaRepository;
import com.vcontrola.vcontrola.service.ContaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contas")
public class ContaController {
    @Autowired
    private ContaService service;

    @PostMapping
    public ResponseEntity<Void> criar(@RequestBody @Valid ContaRequest dados, Authentication auth) {
        var usuario = (Usuario) auth.getPrincipal();

        service.criar(dados, usuario);

        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<ContaResponse>> listar(Authentication auth) {
        var usuario = (Usuario) auth.getPrincipal();

        List<ContaResponse> contas = service.listar(usuario.getId());

        return ResponseEntity.ok(contas);
    }
}
