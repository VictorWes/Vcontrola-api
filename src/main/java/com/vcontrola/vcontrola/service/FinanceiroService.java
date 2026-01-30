package com.vcontrola.vcontrola.service;

import com.vcontrola.vcontrola.controller.request.ItemPlanejamentoRequest;
import com.vcontrola.vcontrola.controller.request.TransacaoRequest;
import com.vcontrola.vcontrola.controller.response.ResumoFinanceiroResponse;
import com.vcontrola.vcontrola.entity.*;
import com.vcontrola.vcontrola.enums.StatusPlanejamento;
import com.vcontrola.vcontrola.enums.StatusTransacaoCartao;
import com.vcontrola.vcontrola.enums.TipoTransacao;
import com.vcontrola.vcontrola.infra.exception.RegraDeNegocioException;
import com.vcontrola.vcontrola.mapper.FinanceiroMapper;
import com.vcontrola.vcontrola.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class FinanceiroService {

    @Autowired private ControleFinanceiroRepository controleRepo;
    @Autowired private ItemPlanejamentoRepository itemRepo;
    @Autowired private ContaRepository contaRepo;
    @Autowired private TipoContaUsuarioRepository tipoContaUsuarioRepo;
    @Autowired private TransacaoService transacaoService;
    @Autowired private FinanceiroMapper mapper;

    public ResumoFinanceiroResponse buscarResumo(Usuario usuario) {
        ControleFinanceiro controle = obterControleDoUsuario(usuario);
        List<ItemPlanejamento> itens = itemRepo.findByControleId(controle.getId());
        return mapper.toDashboardResponse(controle, itens);
    }

    @Transactional
    public void adicionarSaldoVirtual(BigDecimal valor, Usuario usuario) {
        ControleFinanceiro controle = obterControleDoUsuario(usuario);
        controle.setSaldoDisponivel(controle.getSaldoDisponivel().add(valor));
        controleRepo.save(controle);
    }

    @Transactional
    public void criarItem(ItemPlanejamentoRequest dados, Usuario usuario) {
        ControleFinanceiro controle = obterControleDoUsuario(usuario);

        Conta conta = contaRepo.findById(dados.contaDestinoId())
                .orElseThrow(() -> new RuntimeException("Conta destino não encontrada"));

        TipoContaUsuario carteira = tipoContaUsuarioRepo.findById(dados.carteiraId())
                .orElseThrow(() -> new RuntimeException("Carteira não encontrada"));

        ItemPlanejamento item = mapper.toEntity(dados, conta, carteira, controle);
        itemRepo.save(item);
    }


    @Transactional
    public void atualizarItem(UUID id, ItemPlanejamentoRequest dados, Usuario usuario) {
        ItemPlanejamento item = itemRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Item não encontrado"));

        if (!item.getControle().getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("Acesso negado");
        }


        if (item.getStatus() == StatusPlanejamento.GUARDADO) {
            throw new RuntimeException("Desbloqueie o item (cadeado) antes de editar.");
        }


        Conta novaConta = contaRepo.findById(dados.contaDestinoId())
                .orElseThrow(() -> new RuntimeException("Conta destino não encontrada"));

        TipoContaUsuario novaCarteira = tipoContaUsuarioRepo.findById(dados.carteiraId())
                .orElseThrow(() -> new RuntimeException("Carteira não encontrada"));

        item.setValor(dados.valor());
        item.setContaDestino(novaConta);
        item.setCarteira(novaCarteira);

        itemRepo.save(item);
    }


    @Transactional
    public void excluirItem(UUID id, Usuario usuario) {
        ItemPlanejamento item = itemRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Item não encontrado"));

        if (!item.getControle().getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("Acesso negado");
        }

        if (item.getStatus() == StatusPlanejamento.GUARDADO) {
            throw new RuntimeException("Desbloqueie o item (cadeado) antes de excluir para estornar o valor.");
        }

        itemRepo.delete(item);
    }

    @Transactional
    public void alternarStatus(UUID itemId, Usuario usuario) {
        ItemPlanejamento item = itemRepo.findById(itemId)
                .orElseThrow(() -> new RegraDeNegocioException("Item não encontrado"));

        ControleFinanceiro controle = item.getControle();

        if (!controle.getUsuario().getId().equals(usuario.getId())) {
            throw new RegraDeNegocioException("Acesso negado");
        }

        if (item.getStatus() == StatusPlanejamento.PENDENTE) {

            if (controle.getSaldoDisponivel().compareTo(item.getValor()) < 0) {
                throw new RegraDeNegocioException("Saldo ficticio disponivel é insuficiente para esta reserva!");
            }

            controle.setSaldoDisponivel(controle.getSaldoDisponivel().subtract(item.getValor()));

            TransacaoRequest deposito = new TransacaoRequest(
                    "Reserva: " + item.getCarteira().getNome(),
                    item.getValor(),
                    TipoTransacao.RECEITAS,
                    StatusTransacaoCartao.PAGO,
                    LocalDate.now(),
                    item.getContaDestino().getId(),
                    null, null
            );
            transacaoService.criar(deposito, usuario);

            item.setStatus(StatusPlanejamento.GUARDADO);

        } else {

            controle.setSaldoDisponivel(controle.getSaldoDisponivel().add(item.getValor()));

            TransacaoRequest saque = new TransacaoRequest(
                    "Estorno Reserva: " + item.getCarteira().getNome(),
                    item.getValor(),
                    TipoTransacao.GASTOS,
                    StatusTransacaoCartao.PAGO,
                    LocalDate.now(),
                    item.getContaDestino().getId(),
                    null, null
            );
            transacaoService.criar(saque, usuario);

            item.setStatus(StatusPlanejamento.PENDENTE);
        }

        controleRepo.save(controle);
        itemRepo.save(item);
    }

    private ControleFinanceiro obterControleDoUsuario(Usuario usuario) {
        return controleRepo.findByUsuarioId(usuario.getId())
                .orElseGet(() -> {
                    ControleFinanceiro novo = new ControleFinanceiro();
                    novo.setUsuario(usuario);
                    novo.setSaldoDisponivel(BigDecimal.ZERO);
                    return controleRepo.save(novo);
                });
    }

    @Transactional
    public void resgatarParcial(UUID itemId, BigDecimal valorResgate, Usuario usuario) {
        ItemPlanejamento item = itemRepo.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item não encontrado"));

        if (!item.getControle().getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("Acesso negado");
        }

        if (item.getStatus() != StatusPlanejamento.GUARDADO) {
            throw new RuntimeException("Apenas itens guardados podem ter resgate parcial.");
        }

        if (valorResgate.compareTo(item.getValor()) > 0) {
            throw new RuntimeException("O valor do resgate não pode ser maior que o valor guardado.");
        }

        item.setValor(item.getValor().subtract(valorResgate));

        TransacaoRequest saque = new TransacaoRequest(
                "Resgate Parcial: " + item.getCarteira().getNome(),
                valorResgate,
                TipoTransacao.GASTOS,
                StatusTransacaoCartao.PAGO,
                LocalDate.now(),
                item.getContaDestino().getId(),
                null, null
        );
        transacaoService.criar(saque, usuario);


        if (item.getValor().compareTo(BigDecimal.ZERO) == 0) {
            item.setStatus(StatusPlanejamento.PENDENTE);
        }

        itemRepo.save(item);

    }
}