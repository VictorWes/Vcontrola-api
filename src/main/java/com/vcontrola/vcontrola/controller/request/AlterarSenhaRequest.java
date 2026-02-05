package com.vcontrola.vcontrola.controller.request;

public record AlterarSenhaRequest(
        String senhaAtual,
        String novaSenha
) {
}
