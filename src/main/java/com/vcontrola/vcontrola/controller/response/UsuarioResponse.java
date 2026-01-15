package com.vcontrola.vcontrola.controller.response;

import java.util.UUID;

public record UsuarioResponse(
    UUID id,
    String nome,
    String email
) {
}
