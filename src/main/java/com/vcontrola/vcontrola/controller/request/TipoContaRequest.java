package com.vcontrola.vcontrola.controller.request;

import com.vcontrola.vcontrola.enums.TipoConta;
import jakarta.validation.constraints.NotBlank;

public record TipoContaRequest (
        @NotBlank String nome,
        String icone,
        TipoConta comportamento
) {
}
