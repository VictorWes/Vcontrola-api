package com.vcontrola.vcontrola.service;

import com.vcontrola.vcontrola.controller.request.ContaRequest;
import com.vcontrola.vcontrola.controller.response.ContaResponse;
import com.vcontrola.vcontrola.entity.Conta;
import com.vcontrola.vcontrola.entity.Usuario;
import com.vcontrola.vcontrola.mapper.ContaMapper;
import com.vcontrola.vcontrola.repository.ContaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ContaService {
    @Autowired
    private ContaRepository repository;

    @Autowired
    private ContaMapper mapper;

    public void criar(ContaRequest dados, Usuario usuario) {
        if (dados.saldo().doubleValue() < 0) {
            throw new IllegalArgumentException("Saldo inicial não pode ser negativo no cadastro.");
        }

        Conta conta = mapper.toEntity(dados, usuario);

        repository.save(conta);
    }

    public List<ContaResponse> listar(UUID usuarioId) {

        List<Conta> contas = repository.findByUsuarioId(usuarioId);
        return contas.stream()
                .map(mapper::toResponse)
                .toList();
    }

    public void atualizar(UUID id, ContaRequest dados, Usuario usuario) {
        Conta conta = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada"));

        if (!conta.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("Você não tem permissão para alterar esta conta.");
        }

        conta.setNome(dados.nome());
        conta.setSaldo(dados.saldo());

        conta.setTipo(mapper.mapTipo(dados.tipo(), usuario));

        repository.save(conta);
    }

    public void excluir(UUID id, Usuario usuario) {
        Conta conta = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada"));

        if (!conta.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("Você não tem permissão para excluir esta conta.");
        }

        repository.delete(conta);
    }


}
