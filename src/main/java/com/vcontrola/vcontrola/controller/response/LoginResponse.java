package com.vcontrola.vcontrola.controller.response;

public record LoginResponse (
        String token,
        String nome
) {
}
