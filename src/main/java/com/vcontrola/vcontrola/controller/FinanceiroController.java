package com.vcontrola.vcontrola.controller;

import com.vcontrola.vcontrola.controller.request.ItemPlanejamentoRequest;
import com.vcontrola.vcontrola.controller.request.ResgateRequest;
import com.vcontrola.vcontrola.controller.request.SaldoVirtualRequest;
import com.vcontrola.vcontrola.controller.response.ResumoFinanceiroResponse;
import com.vcontrola.vcontrola.entity.Usuario;
import com.vcontrola.vcontrola.service.FinanceiroService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/financeiro")
public class FinanceiroController {

    @Autowired
    private FinanceiroService service;

    @GetMapping
    public ResponseEntity<ResumoFinanceiroResponse> buscarResumo(Authentication auth) {
        Usuario usuario = (Usuario) auth.getPrincipal();
        var resumo = service.buscarResumo(usuario);
        return ResponseEntity.ok(resumo);
    }

    @PostMapping("/saldo")
    public ResponseEntity<Void> adicionarSaldo(@RequestBody @Valid SaldoVirtualRequest request, Authentication auth) {
        Usuario usuario = (Usuario) auth.getPrincipal();
        service.adicionarSaldoVirtual(request.valor(), usuario);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/item")
    public ResponseEntity<Void> criarItem(@RequestBody @Valid ItemPlanejamentoRequest request, Authentication auth) {
        Usuario usuario = (Usuario) auth.getPrincipal();
        service.criarItem(request, usuario);
        return ResponseEntity.ok().build();
    }


    @PutMapping("/item/{id}")
    public ResponseEntity<Void> atualizarItem(@PathVariable UUID id, @RequestBody @Valid ItemPlanejamentoRequest request, Authentication auth) {
        Usuario usuario = (Usuario) auth.getPrincipal();
        service.atualizarItem(id, request, usuario);
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("/item/{id}")
    public ResponseEntity<Void> excluirItem(@PathVariable UUID id, Authentication auth) {
        Usuario usuario = (Usuario) auth.getPrincipal();
        service.excluirItem(id, usuario);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/item/{id}/alternar")
    public ResponseEntity<Void> alternarStatus(@PathVariable UUID id, Authentication auth) {
        Usuario usuario = (Usuario) auth.getPrincipal();
        service.alternarStatus(id, usuario);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/item/{id}/resgatar")
    public ResponseEntity<Void> resgatarParcial(
            @PathVariable UUID id,
            @RequestBody @Valid ResgateRequest request,
            Authentication auth
    ) {
        Usuario usuario = (Usuario) auth.getPrincipal();
        service.resgatarParcial(id, request.valor(), usuario);

        return ResponseEntity.ok().build();
    }
}