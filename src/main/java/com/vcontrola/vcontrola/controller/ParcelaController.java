package com.vcontrola.vcontrola.controller;

import com.vcontrola.vcontrola.controller.request.EstornarParcelaRequest;
import com.vcontrola.vcontrola.controller.request.PagarParcelaRequest;
import com.vcontrola.vcontrola.controller.response.ParcelaResponse;
import com.vcontrola.vcontrola.entity.Usuario;
import com.vcontrola.vcontrola.repository.UsuarioRepository;
import com.vcontrola.vcontrola.service.ParcelaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/parcelas")
public class ParcelaController {
    @Autowired
    private ParcelaService service;

    @Autowired
    private UsuarioRepository usuarioRepository;


    @GetMapping("/compra/{compraId}")
    public ResponseEntity<List<ParcelaResponse>> listarPorCompra(@PathVariable UUID compraId) {
        List<ParcelaResponse> responses = service.listarPorCompra(compraId);
        return ResponseEntity.ok(responses);
    }


    @PostMapping("/{id}/pagar")
    public ResponseEntity<Void> pagar(
            @PathVariable UUID id,
            @RequestBody PagarParcelaRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        service.pagar(id, request.contaId(), usuario);

        return ResponseEntity.noContent().build();
    }


    @PostMapping("/{id}/estornar")
    public ResponseEntity<Void> estornar(
            @PathVariable UUID id,
            @RequestBody EstornarParcelaRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        service.estornar(id, request.contaId(), usuario);

        return ResponseEntity.noContent().build();
    }
}
