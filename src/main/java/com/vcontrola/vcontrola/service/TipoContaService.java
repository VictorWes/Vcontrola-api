package com.vcontrola.vcontrola.service;

import com.vcontrola.vcontrola.controller.request.TipoContaRequest;
import com.vcontrola.vcontrola.controller.response.TipoContaResponse;
import com.vcontrola.vcontrola.entity.TipoContaUsuario;
import com.vcontrola.vcontrola.entity.Usuario;
import com.vcontrola.vcontrola.mapper.TipoContaMapper;
import com.vcontrola.vcontrola.repository.ItemPlanejamentoRepository;
import com.vcontrola.vcontrola.repository.TipoContaUsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TipoContaService {
    @Autowired
    private TipoContaUsuarioRepository repository;
    @Autowired private ItemPlanejamentoRepository planejamentoRepo;
    @Autowired private TipoContaMapper mapper;


    public List<TipoContaResponse> listar(Usuario usuario) {
        List<TipoContaUsuario> entidades = repository.findByUsuario(usuario);
        return entidades.stream()
                .map(mapper::toResponse)
                .toList();
    }


    @Transactional
    public void criar(TipoContaRequest request, Usuario usuario) {
        TipoContaUsuario novoTipo = mapper.toEntity(request, usuario);
        repository.save(novoTipo);
    }


    @Transactional
    public void atualizar(UUID id, String novoNome, Usuario usuario) {
        TipoContaUsuario carteira = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Carteira não encontrada"));


        if (!carteira.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("Acesso negado.");
        }

        carteira.setNome(novoNome);
        repository.save(carteira);
    }


    @Transactional
    public void excluir(UUID id, Usuario usuario) {
        TipoContaUsuario carteira = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Carteira não encontrada"));


        if (!carteira.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("Acesso negado.");
        }


        if (planejamentoRepo.existsByCarteiraId(id)) {
            throw new RuntimeException("Não é possível excluir: Esta carteira está sendo usada em Planejamentos.");
        }

        repository.delete(carteira);
    }
}
