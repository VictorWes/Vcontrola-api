package com.vcontrola.vcontrola.controller;

import com.vcontrola.vcontrola.controller.request.CartaoRequest;
import com.vcontrola.vcontrola.controller.response.CartaoResponse;
import com.vcontrola.vcontrola.entity.Usuario;
import com.vcontrola.vcontrola.repository.UsuarioRepository;
import com.vcontrola.vcontrola.service.CartaoCreditoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/cartoes")
public class CartaoController {
    @Autowired
    private CartaoCreditoService service;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping
    public ResponseEntity<Void> criar(@RequestBody CartaoRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        service.criar(request, usuario);

        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<CartaoResponse>> listar(@AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        List<CartaoResponse> response = service.listar(usuario);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> atualizar(
            @PathVariable UUID id,
            @RequestBody CartaoRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        service.atualizar(id, request, usuario);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        service.excluir(id, usuario);

        return ResponseEntity.noContent().build();
    }
}
