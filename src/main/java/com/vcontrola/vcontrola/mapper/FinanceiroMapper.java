package com.vcontrola.vcontrola.mapper;

import com.vcontrola.vcontrola.controller.request.ItemPlanejamentoRequest;
import com.vcontrola.vcontrola.controller.response.ItemPlanejamentoResponse;
import com.vcontrola.vcontrola.controller.response.ResumoFinanceiroResponse;
import com.vcontrola.vcontrola.entity.Conta;
import com.vcontrola.vcontrola.entity.ControleFinanceiro;
import com.vcontrola.vcontrola.entity.ItemPlanejamento;
import com.vcontrola.vcontrola.entity.TipoContaUsuario;
import com.vcontrola.vcontrola.enums.StatusPlanejamento;
import com.vcontrola.vcontrola.enums.TipoConta;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FinanceiroMapper {

    public ItemPlanejamento toEntity(ItemPlanejamentoRequest request, Conta conta, TipoContaUsuario carteira, ControleFinanceiro controle) {
        ItemPlanejamento item = new ItemPlanejamento();

        item.setCarteira(carteira);
        item.setValor(request.valor());
        item.setStatus(StatusPlanejamento.PENDENTE);
        item.setContaDestino(conta);
        item.setControle(controle);
        return item;
    }

    public ItemPlanejamentoResponse toResponse(ItemPlanejamento entity) {
        return new ItemPlanejamentoResponse(
                entity.getId(),
                entity.getCarteira().getNome(), // Agora funciona! TipoContaUsuario tem getNome()
                entity.getCarteira().getId(),
                entity.getValor(),
                entity.getStatus(),
                entity.getContaDestino().getNome(),
                entity.getContaDestino().getId()
        );
    }

    public ResumoFinanceiroResponse toDashboardResponse(ControleFinanceiro controle, List<ItemPlanejamento> itens) {
        List<ItemPlanejamentoResponse> itensResponse = itens.stream()
                .map(this::toResponse)
                .toList();

        return new ResumoFinanceiroResponse(
                controle.getSaldoDisponivel(),
                itensResponse
        );
    }
}
