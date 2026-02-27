package com.vcontrola.vcontrola.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vcontrola.vcontrola.AbstractIntegrationTest;
import com.vcontrola.vcontrola.controller.request.LoginRequest;
import com.vcontrola.vcontrola.controller.request.UsuarioRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
public class UsuarioControllerIT extends AbstractIntegrationTest{

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Deve cadastrar um novo usuário com sucesso e retornar 201 Created")
    void deveCadastrarUsuarioComSucesso() throws Exception {


        UsuarioRequest request = new UsuarioRequest(
                "Victor Wesley",
                "victor.test@gmail.com",
                "SenhaForte@123"
        );


        String jsonBody = objectMapper.writeValueAsString(request);


        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value("Victor Wesley"))
                .andExpect(jsonPath("$.email").value("victor.test@gmail.com"));
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request ao tentar cadastrar usuário com email inválido")
    void naoDeveCadastrarUsuarioComEmailInvalido() throws Exception {


        UsuarioRequest request = new UsuarioRequest(
                "Victor Hacker",
                "email-invalido-sem-arroba",
                "SenhaForte@123"
        );

        String jsonBody = objectMapper.writeValueAsString(request);


        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve realizar login com sucesso e retornar um Token JWT")
    void deveRealizarLoginComSucesso() throws Exception {


        UsuarioRequest novoUsuario = new UsuarioRequest(
                "Victor Login",
                "login.test@gmail.com",
                "SenhaForte@123"
        );

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(novoUsuario)))
                .andExpect(status().isCreated());


        LoginRequest loginRequest = new LoginRequest(
                "login.test@gmail.com",
                "SenhaForte@123"
        );

        String jsonLogin = objectMapper.writeValueAsString(loginRequest);


        mockMvc.perform(post("/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonLogin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    @DisplayName("Deve acessar rota protegida (/usuarios/me) usando o Token JWT")
    void deveAcessarPerfilComTokenJwt() throws Exception {


        UsuarioRequest usuarioPerfil = new UsuarioRequest(
                "Victor Logado",
                "logado.test@gmail.com",
                "SenhaForte@123"
        );

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioPerfil)))
                .andExpect(status().isCreated());


        LoginRequest loginRequest = new LoginRequest("logado.test@gmail.com", "SenhaForte@123");


        String jsonRespostaLogin = mockMvc.perform(post("/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();


        String tokenJwt = objectMapper.readTree(jsonRespostaLogin).get("token").asText();

        mockMvc.perform(get("/usuarios/me")
                        .header("Authorization", "Bearer " + tokenJwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Victor Logado"))
                .andExpect(jsonPath("$.email").value("logado.test@gmail.com"));
    }
}
