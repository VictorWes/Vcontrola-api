package com.vcontrola.vcontrola.mapper;

import com.vcontrola.vcontrola.controller.request.CartaoRequest;
import com.vcontrola.vcontrola.controller.response.CartaoResponse;
import com.vcontrola.vcontrola.entity.CartaoCredito;
import com.vcontrola.vcontrola.entity.Usuario;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class CartaoCreditoMapper {
    public CartaoCredito toEntity(CartaoRequest request, Usuario usuario) {
        CartaoCredito cartao = new CartaoCredito();
        cartao.setNome(request.nome());
        cartao.setLimiteTotal(request.limite());
        cartao.setLimiteDisponivel(request.limite());
        cartao.setDiaVencimento(request.diaVencimento());
        cartao.setDiaFechamento(request.diaFechamento());
        cartao.setUsuario(usuario);
        return cartao;
    }

    public CartaoResponse toResponse(CartaoCredito cartao) {
        return new CartaoResponse(
                cartao.getId(),
                cartao.getNome(),
                cartao.getLimiteTotal(),
                cartao.getDiaVencimento(),
                cartao.getDiaFechamento(),
                BigDecimal.ZERO
        );
    }
}
