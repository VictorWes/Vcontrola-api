package com.vcontrola.vcontrola.controller;

import com.vcontrola.vcontrola.controller.request.GoogleLoginRequest;
import com.vcontrola.vcontrola.controller.request.LoginRequest;
import com.vcontrola.vcontrola.controller.request.UsuarioRequest;
import com.vcontrola.vcontrola.controller.response.LoginResponse;
import com.vcontrola.vcontrola.controller.response.UsuarioResponse;
import com.vcontrola.vcontrola.service.GoogleLogService;
import com.vcontrola.vcontrola.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin(origins = "http://localhost:4200")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final GoogleLogService googleLogService;

    public UsuarioController(UsuarioService usuarioService, GoogleLogService googleLogService) {
        this.usuarioService = usuarioService;
        this.googleLogService = googleLogService;
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> cadastrar(@RequestBody @Valid UsuarioRequest request) {

        UsuarioResponse novoUsuario = usuarioService.cadastrarUsuario(request);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(novoUsuario.id())
                .toUri();

        return ResponseEntity.created(uri).body(novoUsuario);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {

        LoginResponse response = usuarioService.autenticar(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/google")
    public ResponseEntity loginGoogle(@RequestBody GoogleLoginRequest dados) {
        try {
            String tokenJwt = googleLogService.autenticarGoogle(dados.token());

            // Retornamos no mesmo formato que o Login normal retorna
            return ResponseEntity.ok(new LoginResponse(tokenJwt, "Login Google com sucesso"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao autenticar com Google: " + e.getMessage());
        }
    }
}
