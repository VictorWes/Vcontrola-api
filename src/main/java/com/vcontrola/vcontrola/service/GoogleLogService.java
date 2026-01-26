package com.vcontrola.vcontrola.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.vcontrola.vcontrola.controller.response.LoginResponse;
import com.vcontrola.vcontrola.entity.Usuario;
import com.vcontrola.vcontrola.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Service
public class GoogleLogService {

    @Value("${google.client.id}")
    private  String CLIENT_ID;

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public LoginResponse autenticarGoogle(String tokenGoogle) throws GeneralSecurityException, IOException {


        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(CLIENT_ID))
                .build();


        GoogleIdToken idToken = verifier.verify(tokenGoogle);
        if (idToken == null) {
            throw new IllegalArgumentException("Token do Google inválido.");
        }


        GoogleIdToken.Payload payload = idToken.getPayload();
        String email = payload.getEmail();
        String nome = (String) payload.get("name");


        Usuario usuario = repository.findByEmail(email).orElse(null);

        if (usuario == null) {

            usuario = new Usuario();
            usuario.setNome(nome);
            usuario.setEmail(email);
            usuario.setSenha(passwordEncoder.encode("GOOGLE_AUTH_" + java.util.UUID.randomUUID().toString()));

            repository.save(usuario);
        }


        String tokenJwt = tokenService.gerarToken(usuario);


        return new LoginResponse(tokenJwt, usuario.getNome());
    }
}
