package com.vcontrola.vcontrola.controller;

import com.vcontrola.vcontrola.controller.request.CompraRequest;
import com.vcontrola.vcontrola.controller.response.CompraResponse;
import com.vcontrola.vcontrola.service.CompraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
    public ResponseEntity<Page<CompraResponse>> listarPorCartao(
            @PathVariable UUID cartaoId,
            @PageableDefault(page = 0, size = 5, sort = "dataCompra", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(service.listarPorCartao(cartaoId, pageable));
    }
    @PutMapping("/{id}")
    public ResponseEntity<Void> editar(@PathVariable UUID id, @RequestBody CompraRequest request) {
        service.editar(id, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @PathVariable UUID id,
            @RequestParam(required = false) UUID contaId) { // <--- Conta para onde o dinheiro volta

        service.excluir(id, contaId);
        return ResponseEntity.noContent().build();
    }
}
