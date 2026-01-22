package com.vcontrola.vcontrola.service;

import com.vcontrola.vcontrola.controller.request.ItemPlanejamentoRequest;
import com.vcontrola.vcontrola.controller.request.TransacaoRequest;
import com.vcontrola.vcontrola.controller.response.ResumoFinanceiroResponse;
import com.vcontrola.vcontrola.entity.*;
import com.vcontrola.vcontrola.enums.StatusPlanejamento;
import com.vcontrola.vcontrola.enums.StatusTransacaoCartao;
import com.vcontrola.vcontrola.enums.TipoConta;
import com.vcontrola.vcontrola.enums.TipoTransacao;
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

    @Autowired
    private TipoContaUsuarioRepository tipoContaUsuarioRepo;

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
    public void alternarStatus(UUID itemId, Usuario usuario) {
        ItemPlanejamento item = itemRepo.findById(itemId).orElseThrow();
        ControleFinanceiro controle = item.getControle();

        if (!controle.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("Acesso negado");
        }

        if (item.getStatus() == StatusPlanejamento.PENDENTE) {
            // Guardar
            if (controle.getSaldoDisponivel().compareTo(item.getValor()) < 0) {
                throw new RuntimeException("Saldo virtual insuficiente!");
            }
            controle.setSaldoDisponivel(controle.getSaldoDisponivel().subtract(item.getValor()));

            TransacaoRequest deposito = new TransacaoRequest(
                    "Reserva: " + item.getCarteira().getNome(), // Aqui usa o nome da carteira
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
            // Estornar
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
}