package com.vcontrola.vcontrola.controller.request;

import jakarta.validation.constraints.NotBlank;

public record AtualizarCarteiraRequest (
        @NotBlank(message = "O nome da carteira é obrigatório")
        String nome
) {
}
