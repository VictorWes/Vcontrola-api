package com.vcontrola.vcontrola.service;

import com.vcontrola.vcontrola.controller.request.CartaoRequest;
import com.vcontrola.vcontrola.controller.response.CartaoResponse;
import com.vcontrola.vcontrola.entity.CartaoCredito;
import com.vcontrola.vcontrola.entity.Usuario;
import com.vcontrola.vcontrola.mapper.CartaoCreditoMapper;
import com.vcontrola.vcontrola.repository.CartaoCreditoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartaoCreditoService {
    @Autowired
    private CartaoCreditoRepository repository;

    @Autowired
    private CartaoCreditoMapper mapper;

    public void criar(CartaoRequest request, Usuario usuario) {
        CartaoCredito cartao = mapper.toEntity(request, usuario);
        repository.save(cartao);
    }

    public List<CartaoResponse> listar(Usuario usuario) {
        List<CartaoCredito> cartoes = repository.findByUsuario(usuario);

        return cartoes.stream()
                .map(mapper::toResponse)
                .toList();
    }
}
