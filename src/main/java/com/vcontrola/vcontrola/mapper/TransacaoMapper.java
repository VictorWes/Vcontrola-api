package com.vcontrola.vcontrola.mapper;

import com.vcontrola.vcontrola.controller.request.TransacaoRequest;
import com.vcontrola.vcontrola.controller.response.TransacaoResponse;
import com.vcontrola.vcontrola.entity.Conta;
import com.vcontrola.vcontrola.entity.Transacao;
import com.vcontrola.vcontrola.entity.Usuario;
import com.vcontrola.vcontrola.enums.StatusTransacaoCartao;
import com.vcontrola.vcontrola.enums.TipoTransacao;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class TransacaoMapper {

    public Transacao toEntity(TransacaoRequest request) {
        Transacao transacao = new Transacao();
        transacao.setDescricao(request.descricao());
        transacao.setValor(request.valor());
        transacao.setTipo(request.tipo());
        transacao.setStatus(request.status());
        transacao.setData(request.data());
        transacao.setNumeroParcela(request.numeroParcela());

        return transacao;
    }

    public Transacao criarTransacaoPagamento(
            String descricao,
            BigDecimal valor,
            TipoTransacao tipo,
            Conta conta,
            StatusTransacaoCartao status,
            String numeroParcela
    ) {
        Transacao transacao = new Transacao();

        transacao.setDescricao(descricao);
        transacao.setValor(valor);
        transacao.setTipo(tipo);
        transacao.setConta(conta);
        transacao.setStatus(status);
        transacao.setData(LocalDate.now());
        transacao.setNumeroParcela(numeroParcela);

        return transacao;
    }

    public TransacaoResponse toResponse(Transacao entity) {
        return new TransacaoResponse(
                entity.getId(),
                entity.getDescricao(),
                entity.getValor(),
                entity.getTipo(),
                entity.getStatus(),
                entity.getData(),
                entity.getConta().getId(),
                entity.getConta().getNome(), // Aqui pegamos o nome da conta para facilitar o Front
                entity.getNumeroParcela()
        );
    }
}
