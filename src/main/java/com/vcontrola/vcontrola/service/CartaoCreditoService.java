package com.vcontrola.vcontrola.service;

import com.vcontrola.vcontrola.controller.request.CartaoRequest;
import com.vcontrola.vcontrola.controller.response.CartaoResponse;
import com.vcontrola.vcontrola.entity.CartaoCredito;
import com.vcontrola.vcontrola.entity.Usuario;
import com.vcontrola.vcontrola.mapper.CartaoCreditoMapper;
import com.vcontrola.vcontrola.repository.CartaoCreditoRepository;
import com.vcontrola.vcontrola.repository.ParcelaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class CartaoCreditoService {
    @Autowired
    private CartaoCreditoRepository repository;

    @Autowired
    private ParcelaRepository parcelaRepository;

    @Autowired
    private CartaoCreditoMapper mapper;

    public void criar(CartaoRequest request, Usuario usuario) {
        CartaoCredito cartao = mapper.toEntity(request, usuario);
        repository.save(cartao);
    }

    public List<CartaoResponse> listar(Usuario usuario) {
        List<CartaoCredito> cartoes = repository.findByUsuario(usuario);

        return cartoes.stream()
                .map(cartao -> {
                    CartaoResponse response = mapper.toResponse(cartao);

                    LocalDate hoje = LocalDate.now();
                    LocalDate inicioMes = hoje.withDayOfMonth(1);
                    LocalDate fimMes = hoje.withDayOfMonth(hoje.lengthOfMonth());

                    BigDecimal valorFatura = parcelaRepository.somarFaturaAtual(
                            cartao.getId(), inicioMes, fimMes
                    );


                    return new CartaoResponse(
                            response.id(),
                            response.nome(),
                            response.limiteTotal(),
                            response.limiteDisponivel(),
                            response.diaVencimento(),
                            response.diaFechamento(),
                            valorFatura
                    );
                })
                .toList();
    }

    @Transactional
    public void atualizar(UUID id, CartaoRequest request, Usuario usuario) {
        CartaoCredito cartao = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cartão não encontrado"));

        if (!cartao.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("Acesso negado a este cartão");
        }

        if (request.limite().compareTo(cartao.getLimiteTotal()) != 0) {
            BigDecimal diferenca = request.limite().subtract(cartao.getLimiteTotal());
            cartao.setLimiteTotal(request.limite());
            cartao.setLimiteDisponivel(cartao.getLimiteDisponivel().add(diferenca));
        }

        mapper.updateEntity(cartao, request);

        repository.save(cartao);
    }

    @Transactional
    public void excluir(UUID id, Usuario usuario) {
        CartaoCredito cartao = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cartão não encontrado"));

        if (!cartao.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("Acesso negado a este cartão");
        }

        repository.delete(cartao);
    }
}
