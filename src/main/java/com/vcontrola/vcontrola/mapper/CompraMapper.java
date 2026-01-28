package com.vcontrola.vcontrola.mapper;

import com.vcontrola.vcontrola.controller.request.CompraRequest;
import com.vcontrola.vcontrola.controller.response.CompraResponse;
import com.vcontrola.vcontrola.entity.CartaoCredito;
import com.vcontrola.vcontrola.entity.Compra;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class CompraMapper {
    public Compra toEntity(CompraRequest request, CartaoCredito cartao) {
        Compra compra = new Compra();
        compra.setNome(request.nome());
        compra.setValorTotal(request.valorTotal());
        compra.setQtdeParcelas(request.qtdeParcelas());
        compra.setDataCompra(request.dataCompra());
        compra.setCartao(cartao);
        return compra;
    }

    public CompraResponse toResponse(Compra compra, boolean isQuitada) {

        BigDecimal valorParcela = BigDecimal.ZERO;

        if (compra.getQtdeParcelas() > 0) {
            valorParcela = compra.getValorTotal()
                    .divide(BigDecimal.valueOf(compra.getQtdeParcelas()), 2, RoundingMode.HALF_UP);
        }

        return new CompraResponse(
                compra.getId(),
                compra.getNome(),
                compra.getValorTotal(),
                compra.getQtdeParcelas(),
                valorParcela,
                compra.getDataCompra(),
                isQuitada

        );
    }

}
