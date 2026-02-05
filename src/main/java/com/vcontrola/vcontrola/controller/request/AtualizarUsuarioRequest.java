package com.vcontrola.vcontrola.controller.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AtualizarUsuarioRequest(
        @NotBlank(message = "O nome não pode estar vazio")
        String nome,
        @Email(message = "Formato de e-mail inválido")
        String email
) {
}
