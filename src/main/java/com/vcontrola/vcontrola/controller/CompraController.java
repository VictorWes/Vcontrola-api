package com.vcontrola.vcontrola.controller;

import com.vcontrola.vcontrola.controller.request.CompraRequest;
import com.vcontrola.vcontrola.controller.response.CompraResponse;
import com.vcontrola.vcontrola.service.CompraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/compras")
public class CompraController {
    @Autowired
    private CompraService service;

    @PostMapping
    public ResponseEntity<Void> criar(@RequestBody CompraRequest request) {
        service.criar(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/cartao/{cartaoId}")
    public ResponseEntity<List<CompraResponse>> listar(@PathVariable UUID cartaoId) {
        List<CompraResponse> response = service.listarPorCartao(cartaoId);
        return ResponseEntity.ok(response);
    }
}
