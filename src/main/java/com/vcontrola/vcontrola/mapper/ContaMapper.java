package com.vcontrola.vcontrola.mapper;

import com.vcontrola.vcontrola.controller.request.ContaRequest;
import com.vcontrola.vcontrola.controller.response.ContaResponse;
import com.vcontrola.vcontrola.entity.Conta;
import org.springframework.stereotype.Component;

@Component
public class ContaMapper {
    public Conta toEntity(ContaRequest request) {
        Conta conta = new Conta();
        conta.setNome(request.nome());
        conta.setSaldo(request.saldo());
        conta.setTipo(request.tipo());
        return conta;
    }

    public ContaResponse toResponse(Conta conta) {
        return new ContaResponse(
                conta.getId(),
                conta.getNome(),
                conta.getSaldo(),
                conta.getTipo()
        );
    }
}
