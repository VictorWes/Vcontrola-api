package com.vcontrola.vcontrola.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DefinirNovaSenhaRequest(

        @NotBlank String token,
        @NotBlank @Size(min = 6) String novaSenha
) {
}
