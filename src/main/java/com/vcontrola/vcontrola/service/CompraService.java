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
}
