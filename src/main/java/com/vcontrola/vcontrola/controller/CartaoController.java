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
}
