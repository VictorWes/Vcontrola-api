package com.vcontrola.vcontrola.service;

import com.vcontrola.vcontrola.controller.response.ParcelaResponse;
import com.vcontrola.vcontrola.entity.*;
import com.vcontrola.vcontrola.enums.StatusTransacaoCartao;
import com.vcontrola.vcontrola.enums.TipoTransacao;
import com.vcontrola.vcontrola.infra.exception.RegraDeNegocioException;
import com.vcontrola.vcontrola.mapper.ParcelaMapper;
import com.vcontrola.vcontrola.mapper.TransacaoMapper;
import com.vcontrola.vcontrola.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class ParcelaService {
    @Autowired
    private ParcelaRepository repository;

    @Autowired
    private ContaRepository contaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CartaoCreditoRepository cartaoRepository;

    @Autowired
    private TransacaoMapper transacaoMapper;

    @Autowired
    private TransacaoRepository transacaoRepository;

    @Autowired
    private ParcelaMapper mapper;

    public List<ParcelaResponse> listarPorCompra(UUID compraId) {
        return repository.findByCompraIdOrderByNumeroParcelaAsc(compraId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional
    public void pagar(UUID idParcela, UUID idConta, Usuario usuario) {

        Parcela parcela = repository.findById(idParcela)
                .orElseThrow(() -> new RuntimeException("Parcela não encontrada"));

        if (parcela.isPaga()) {
            throw new RegraDeNegocioException("Esta parcela já foi paga!");
        }

        Conta conta = contaRepository.findById(idConta)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada"));


        if (!conta.getUsuario().getId().equals(usuario.getId()) ||
                !parcela.getCompra().getCartao().getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("Acesso negado");
        }


        if (conta.getSaldo().compareTo(parcela.getValorParcela()) < 0) {
            throw new RegraDeNegocioException("Saldo insuficiente na conta " + conta.getNome() + " para realizar este pagamento.");
        }



        conta.setSaldo(conta.getSaldo().subtract(parcela.getValorParcela()));


        CartaoCredito cartao = parcela.getCompra().getCartao();
        BigDecimal novoLimite = cartao.getLimiteDisponivel().add(parcela.getValorParcela());


        if (novoLimite.compareTo(cartao.getLimiteTotal()) > 0) {
            novoLimite = cartao.getLimiteTotal();
        }
        cartao.setLimiteDisponivel(novoLimite);


        parcela.setPaga(true);


        String numParcelaFormatado = parcela.getNumeroParcela() + "/" + parcela.getCompra().getQtdeParcelas();


        String descricao = "Pagamento: " + parcela.getCompra().getNome();


        Transacao novaTransacao = transacaoMapper.criarTransacaoPagamento(
                descricao,
                parcela.getValorParcela(),
                TipoTransacao.GASTOS,
                conta,
                StatusTransacaoCartao.PAGO,
                numParcelaFormatado
        );


        transacaoRepository.save(novaTransacao);


        contaRepository.save(conta);
        cartaoRepository.save(cartao);
        repository.save(parcela);
    }

    @Transactional
    public void estornar(UUID idParcela, UUID idConta, Usuario usuario) {

        Parcela parcela = repository.findById(idParcela)
                .orElseThrow(() -> new RuntimeException("Parcela não encontrada"));

        if (!parcela.isPaga()) {
            throw new RuntimeException("Esta parcela não está paga, impossível estornar!");
        }


        Conta conta = contaRepository.findById(idConta)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada"));

        if (!conta.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("Acesso negado");
        }

        conta.setSaldo(conta.getSaldo().add(parcela.getValorParcela()));
        CartaoCredito cartao = parcela.getCompra().getCartao();
        BigDecimal novoLimite = cartao.getLimiteDisponivel().subtract(parcela.getValorParcela());

        if (novoLimite.compareTo(BigDecimal.ZERO) < 0) {
            novoLimite = BigDecimal.ZERO;
        }
        cartao.setLimiteDisponivel(novoLimite);
        parcela.setPaga(false);

        contaRepository.save(conta);
        cartaoRepository.save(cartao);
        repository.save(parcela);
    }


}
