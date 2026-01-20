package com.vcontrola.vcontrola.service;

import com.vcontrola.vcontrola.controller.request.TransacaoRequest;
import com.vcontrola.vcontrola.controller.response.TransacaoResponse;
import com.vcontrola.vcontrola.entity.Conta;
import com.vcontrola.vcontrola.entity.Transacao;
import com.vcontrola.vcontrola.entity.Usuario;
import com.vcontrola.vcontrola.enums.StatusTransacaoCartao;
import com.vcontrola.vcontrola.enums.TipoTransacao;
import com.vcontrola.vcontrola.mapper.TransacaoMapper;
import com.vcontrola.vcontrola.repository.ContaRepository;
import com.vcontrola.vcontrola.repository.TransacaoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransacaoService {

    @Autowired
    private TransacaoRepository repository;

    @Autowired
    private ContaRepository contaRepository;

    @Autowired
    private TransacaoMapper mapper;

    @Transactional
    public void criar(TransacaoRequest dados, Usuario usuario) {


        Conta conta = contaRepository.findById(dados.contaId())
                .orElseThrow(() -> new RuntimeException("Conta não encontrada"));

        if (!conta.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("Esta conta não pertence ao usuário logado.");
        }

        Transacao transacao = mapper.toEntity(dados);
        transacao.setConta(conta);


        if (dados.status() == StatusTransacaoCartao.PAGO) {

            if (dados.tipo() == TipoTransacao.RECEITAS) {
                conta.setSaldo(conta.getSaldo().add(dados.valor()));

            } else if (dados.tipo() == TipoTransacao.GASTOS) {
                conta.setSaldo(conta.getSaldo().subtract(dados.valor()));
            }

            contaRepository.save(conta);
        }

        repository.save(transacao);
    }

    public List<TransacaoResponse> listar(Usuario usuario) {

        List<Transacao> transacoes = repository.findAllByUsuarioId(usuario.getId());
        return transacoes.stream()
                .map(mapper::toResponse)
                .toList();
    }
}
