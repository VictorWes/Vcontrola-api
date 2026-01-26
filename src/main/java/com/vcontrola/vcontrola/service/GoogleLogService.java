package com.vcontrola.vcontrola.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
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

    public String autenticarGoogle(String tokenGoogle) throws GeneralSecurityException, IOException {
        // 1. Configura a validação do Google
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(CLIENT_ID))
                .build();

        // 2. Valida o Token recebido do Front
        GoogleIdToken idToken = verifier.verify(tokenGoogle);
        if (idToken == null) {
            throw new IllegalArgumentException("Token do Google inválido.");
        }

        // 3. Extrai os dados do usuário do Token
        GoogleIdToken.Payload payload = idToken.getPayload();
        String email = payload.getEmail();
        String nome = (String) payload.get("name");

        // 4. Verifica se o usuário já existe no banco
        Usuario usuario = repository.findByEmail(email).orElse(null);

        if (usuario == null) {
            // 5. Se não existe, cria um novo usuário automaticamente
            usuario = new Usuario();
            usuario.setNome(nome);
            usuario.setEmail(email);
            // Gera uma senha aleatória forte pois ele loga pelo Google (ninguém vai usar essa senha)
            usuario.setSenha(passwordEncoder.encode("GOOGLE_AUTH_" + java.util.UUID.randomUUID().toString()));

            repository.save(usuario);
        }

        // 6. Gera o SEU token JWT (o mesmo do login normal) e devolve
        return tokenService.gerarToken(usuario);
    }
}
