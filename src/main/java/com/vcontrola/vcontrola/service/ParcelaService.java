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
        // --------------------------------------------------------------------
        // 1. BUSCAS E VALIDAÇÕES
        // --------------------------------------------------------------------
        Parcela parcela = repository.findById(idParcela)
                .orElseThrow(() -> new RuntimeException("Parcela não encontrada"));

        if (parcela.isPaga()) {
            throw new RegraDeNegocioException("Esta parcela já foi paga!");
        }

        Conta conta = contaRepository.findById(idConta)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada"));

        // Validação de Segurança (Se a conta e o cartão pertencem ao usuário logado)
        if (!conta.getUsuario().getId().equals(usuario.getId()) ||
                !parcela.getCompra().getCartao().getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("Acesso negado");
        }

        // Validação de Saldo (Retorna erro 422 para o Front tratar)
        if (conta.getSaldo().compareTo(parcela.getValorParcela()) < 0) {
            throw new RegraDeNegocioException("Saldo insuficiente na conta " + conta.getNome() + " para realizar este pagamento.");
        }

        // --------------------------------------------------------------------
        // 2. LÓGICA FINANCEIRA (Débito e Limite)
        // --------------------------------------------------------------------

        // Debita da conta
        conta.setSaldo(conta.getSaldo().subtract(parcela.getValorParcela()));

        // Restaura limite do cartão
        CartaoCredito cartao = parcela.getCompra().getCartao();
        BigDecimal novoLimite = cartao.getLimiteDisponivel().add(parcela.getValorParcela());

        // Trava para não exceder o limite total
        if (novoLimite.compareTo(cartao.getLimiteTotal()) > 0) {
            novoLimite = cartao.getLimiteTotal();
        }
        cartao.setLimiteDisponivel(novoLimite);

        // Marca como paga
        parcela.setPaga(true);

        // --------------------------------------------------------------------
        // 3. CRIAÇÃO DO HISTÓRICO (Usando seu Mapper Novo)
        // --------------------------------------------------------------------

        // Formata a string "1/10"
        String numParcelaFormatado = parcela.getNumeroParcela() + "/" + parcela.getCompra().getQtdeParcelas();

        // Cria a descrição
        String descricao = "Pagamento: " + parcela.getCompra().getNome();

        // Chama o mapper passando os parâmetros soltos
        Transacao novaTransacao = transacaoMapper.criarTransacaoPagamento(
                descricao,                      // String descricao
                parcela.getValorParcela(),      // BigDecimal valor
                TipoTransacao.GASTOS,          // TipoTransacao tipo
                conta,                          // Conta conta (Objeto)
                StatusTransacaoCartao.PAGO,     // StatusTransacaoCartao status
                numParcelaFormatado             // String numeroParcela
        );

        // Salva a transação gerada
        transacaoRepository.save(novaTransacao);

        // --------------------------------------------------------------------
        // 4. PERSISTÊNCIA FINAL
        // --------------------------------------------------------------------
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
