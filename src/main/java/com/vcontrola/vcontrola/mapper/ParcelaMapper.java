package com.vcontrola.vcontrola.mapper;

import com.vcontrola.vcontrola.controller.response.ParcelaResponse;
import com.vcontrola.vcontrola.entity.Parcela;
import org.springframework.stereotype.Component;

@Component
public class ParcelaMapper {
    public ParcelaResponse toResponse(Parcela parcela) {
        return new ParcelaResponse(
                parcela.getId(),
                parcela.getNumeroParcela(),
                parcela.getValorParcela(),
                parcela.getDataVencimento(),
                parcela.isPaga()
        );
    }
}
