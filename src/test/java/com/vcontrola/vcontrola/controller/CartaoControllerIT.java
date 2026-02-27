package com.vcontrola.vcontrola.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vcontrola.vcontrola.AbstractIntegrationTest;
import com.vcontrola.vcontrola.controller.request.CartaoRequest;
import com.vcontrola.vcontrola.controller.request.LoginRequest;
import com.vcontrola.vcontrola.controller.request.UsuarioRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@Transactional
public class CartaoControllerIT extends AbstractIntegrationTest{
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String tokenJwt;

    @BeforeEach
    void setup() throws Exception {

        UsuarioRequest usuarioCartao = new UsuarioRequest(
                "Dono do Cartao",
                "cartao@gmail.com",
                "SenhaForte@123"
        );

        mockMvc.perform(post("/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioCartao)));

        LoginRequest loginRequest = new LoginRequest("cartao@gmail.com", "SenhaForte@123");
        String jsonResposta = mockMvc.perform(post("/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andReturn().getResponse().getContentAsString();

        this.tokenJwt = objectMapper.readTree(jsonResposta).get("token").asText();
    }

    @Test
    @DisplayName("Deve cadastrar um novo cartão de crédito com sucesso")
    void deveCadastrarCartaoComSucesso() throws Exception {

        CartaoRequest cartaoRequest = new CartaoRequest(
                "Nubank",
                new BigDecimal("5000.00"),
                5,
                25
        );

        String jsonCartao = objectMapper.writeValueAsString(cartaoRequest);
        mockMvc.perform(post("/cartoes")
                        .header("Authorization", "Bearer " + this.tokenJwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonCartao))
                .andExpect(status().isOk()); // O seu controller retorna 200 OK
    }

    @Test
    @DisplayName("Deve listar os cartões do usuário logado")
    void deveListarCartoesDoUsuario() throws Exception {


        CartaoRequest cartaoRequest = new CartaoRequest("Itaú", new BigDecimal("10000.00"), 10, 1);
        mockMvc.perform(post("/cartoes")
                .header("Authorization", "Bearer " + this.tokenJwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cartaoRequest)));

        mockMvc.perform(get("/cartoes")
                        .header("Authorization", "Bearer " + this.tokenJwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Itaú"))
                .andExpect(jsonPath("$[0].limiteTotal").value(10000.00));
    }
}
