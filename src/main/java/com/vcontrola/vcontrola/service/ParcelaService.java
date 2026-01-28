package com.vcontrola.vcontrola.service;

import com.vcontrola.vcontrola.controller.response.ParcelaResponse;
import com.vcontrola.vcontrola.entity.CartaoCredito;
import com.vcontrola.vcontrola.entity.Conta;
import com.vcontrola.vcontrola.entity.Parcela;
import com.vcontrola.vcontrola.entity.Usuario;
import com.vcontrola.vcontrola.mapper.ParcelaMapper;
import com.vcontrola.vcontrola.repository.CartaoCreditoRepository;
import com.vcontrola.vcontrola.repository.ContaRepository;
import com.vcontrola.vcontrola.repository.ParcelaRepository;
import com.vcontrola.vcontrola.repository.UsuarioRepository;
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
            throw new RuntimeException("Esta parcela já foi paga!");
        }

        Conta conta = contaRepository.findById(idConta)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada"));

        if (!conta.getUsuario().getId().equals(usuario.getId()) ||
                !parcela.getCompra().getCartao().getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("Acesso negado");
        }

        if (conta.getSaldo().compareTo(parcela.getValorParcela()) < 0) {
            throw new RuntimeException("Saldo insuficiente na conta " + conta.getNome());
        }

        conta.setSaldo(conta.getSaldo().subtract(parcela.getValorParcela()));

        CartaoCredito cartao = parcela.getCompra().getCartao();
        BigDecimal novoLimite = cartao.getLimiteDisponivel().add(parcela.getValorParcela());

        if (novoLimite.compareTo(cartao.getLimiteTotal()) > 0) {
            novoLimite = cartao.getLimiteTotal();
        }
        cartao.setLimiteDisponivel(novoLimite);
        parcela.setPaga(true);

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
