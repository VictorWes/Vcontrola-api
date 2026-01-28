package com.vcontrola.vcontrola.service;

import com.vcontrola.vcontrola.controller.request.CompraRequest;
import com.vcontrola.vcontrola.controller.response.CompraResponse;
import com.vcontrola.vcontrola.entity.CartaoCredito;
import com.vcontrola.vcontrola.entity.Compra;
import com.vcontrola.vcontrola.entity.Parcela;
import com.vcontrola.vcontrola.infra.exception.RegraDeNegocioException;
import com.vcontrola.vcontrola.mapper.CompraMapper;
import com.vcontrola.vcontrola.repository.CartaoCreditoRepository;
import com.vcontrola.vcontrola.repository.CompraRepository;
import com.vcontrola.vcontrola.repository.ContaRepository;
import com.vcontrola.vcontrola.repository.ParcelaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CompraService {
    @Autowired
    private CompraRepository compraRepository;

    @Autowired
    private CartaoCreditoRepository cartaoRepository;

    @Autowired
    private CompraMapper mapper;

    @Autowired
    private ParcelaRepository parcelaRepository;

    @Autowired
    private ContaRepository contaRepository;

    @Transactional
    public void criar(CompraRequest request) {

        CartaoCredito cartao = cartaoRepository.findById(request.cartaoId())
                .orElseThrow(() -> new RuntimeException("Cartão não encontrado"));


        if (cartao.getLimiteDisponivel().compareTo(request.valorTotal()) < 0) {
            throw new RegraDeNegocioException("Limite insuficiente para esta compra!");
        }
        Compra compra = mapper.toEntity(request, cartao);
        List<Parcela> parcelas = new ArrayList<>();
        BigDecimal valorParcela = request.valorTotal()
                .divide(BigDecimal.valueOf(request.qtdeParcelas()), 2, RoundingMode.HALF_UP);
        LocalDate dataBaseVencimento = request.dataCompra();

        if (dataBaseVencimento.getDayOfMonth() > cartao.getDiaFechamento()) {
            dataBaseVencimento = dataBaseVencimento.plusMonths(1);
        }

        for (int i = 1; i <= request.qtdeParcelas(); i++) {
            Parcela p = new Parcela();
            p.setNumeroParcela(i);
            p.setValorParcela(valorParcela);
            p.setPaga(false);

            LocalDate dataVencimento = dataBaseVencimento.plusMonths(i - 1)
                    .withDayOfMonth(Math.min(cartao.getDiaVencimento(), 28));

            p.setDataVencimento(dataVencimento);

            p.setCompra(compra);
            parcelas.add(p);
        }

        compra.setParcelas(parcelas);
        compraRepository.save(compra);

        cartao.setLimiteDisponivel(cartao.getLimiteDisponivel().subtract(request.valorTotal()));
        cartaoRepository.save(cartao);
    }

    public List<CompraResponse> listarPorCartao(UUID cartaoId) {
        List<Compra> compras = compraRepository.findByCartaoIdOrderByDataCompraDesc(cartaoId);

        return compras.stream()
                .map(compra -> {

                    boolean existePendencia = parcelaRepository.existsByCompraIdAndPagaFalse(compra.getId());
                    boolean isQuitada = !existePendencia;

                    return mapper.toResponse(compra, isQuitada);
                })
                .toList();
    }

    @Transactional
    public void editar(UUID id, CompraRequest request) {
        Compra compra = compraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Compra não encontrada"));

        boolean temParcelaPaga = compra.getParcelas().stream().anyMatch(Parcela::isPaga);

        if (temParcelaPaga) {

            if (compra.getValorTotal().compareTo(request.valorTotal()) != 0 ||
                    !compra.getQtdeParcelas().equals(request.qtdeParcelas())) {
                throw new RegraDeNegocioException("Não é possível alterar valor ou parcelas de uma compra que já possui pagamentos efetuados. Exclua os pagamentos primeiro.");
            }
        } else {



            CartaoCredito cartao = compra.getCartao();
            cartao.setLimiteDisponivel(cartao.getLimiteDisponivel().add(compra.getValorTotal()));


            if (cartao.getLimiteDisponivel().compareTo(request.valorTotal()) < 0) {
                throw new RegraDeNegocioException("Limite insuficiente para o novo valor!");
            }


            cartao.setLimiteDisponivel(cartao.getLimiteDisponivel().subtract(request.valorTotal()));
            cartaoRepository.save(cartao);


            compra.setValorTotal(request.valorTotal());
            compra.setQtdeParcelas(request.qtdeParcelas());


            parcelaRepository.deleteAll(compra.getParcelas());
            criarNovasParcelas(compra, request.qtdeParcelas(), request.dataCompra(), cartao);
        }


        compra.setNome(request.nome());
        compra.setDataCompra(request.dataCompra());

        compraRepository.save(compra);
    }

    @Transactional
    public void excluir(UUID id, UUID contaId) {
        Compra compra = compraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Compra não encontrada"));

        CartaoCredito cartao = compra.getCartao();


        BigDecimal valorJaPago = BigDecimal.ZERO;

        for (Parcela p : compra.getParcelas()) {
            if (p.isPaga()) {
                valorJaPago = valorJaPago.add(p.getValorParcela());
            }
        }

        if (valorJaPago.compareTo(BigDecimal.ZERO) > 0) {
            if (contaId == null) {
                throw new RegraDeNegocioException("Esta compra tem parcelas pagas. Informe a conta para estorno do valor.");
            }

            var conta = contaRepository.findById(contaId)
                    .orElseThrow(() -> new RegraDeNegocioException("Conta para estorno não encontrada"));

            conta.setSaldo(conta.getSaldo().add(valorJaPago));
            contaRepository.save(conta);
        }


        cartao.setLimiteDisponivel(cartao.getLimiteDisponivel().add(compra.getValorTotal()));


        if (cartao.getLimiteDisponivel().compareTo(cartao.getLimiteTotal()) > 0) {
            cartao.setLimiteDisponivel(cartao.getLimiteTotal());
        }

        cartaoRepository.save(cartao);


        compraRepository.delete(compra);
    }


    private void criarNovasParcelas(Compra compra, int qtde, LocalDate dataCompra, CartaoCredito cartao) {
        BigDecimal valorParcela = compra.getValorTotal()
                .divide(BigDecimal.valueOf(qtde), 2, RoundingMode.HALF_UP);

        LocalDate dataBaseVencimento = dataCompra;
        if (dataBaseVencimento.getDayOfMonth() > cartao.getDiaFechamento()) {
            dataBaseVencimento = dataBaseVencimento.plusMonths(1);
        }

        List<Parcela> novasParcelas = new ArrayList<>();
        for (int i = 1; i <= qtde; i++) {
            Parcela p = new Parcela();
            p.setNumeroParcela(i);
            p.setValorParcela(valorParcela);
            p.setPaga(false);
            LocalDate dataVencimento = dataBaseVencimento.plusMonths(i - 1)
                    .withDayOfMonth(Math.min(cartao.getDiaVencimento(), 28));
            p.setDataVencimento(dataVencimento);
            p.setCompra(compra);
            novasParcelas.add(p);
        }
        compra.setParcelas(novasParcelas);
    }
}
