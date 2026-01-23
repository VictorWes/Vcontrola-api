package com.vcontrola.vcontrola.mapper;

import com.vcontrola.vcontrola.controller.request.ContaRequest;
import com.vcontrola.vcontrola.controller.response.ContaResponse;
import com.vcontrola.vcontrola.entity.Conta;
import com.vcontrola.vcontrola.entity.TipoContaUsuario;
import com.vcontrola.vcontrola.entity.Usuario;
import com.vcontrola.vcontrola.repository.TipoContaUsuarioRepository;
import org.springframework.stereotype.Component;

@Component
public class ContaMapper {

    private final TipoContaUsuarioRepository tipoRepo;
    private final TipoContaMapper tipoContaMapper;


    public ContaMapper(TipoContaUsuarioRepository tipoRepo, TipoContaMapper tipoContaMapper) {
        this.tipoRepo = tipoRepo;
        this.tipoContaMapper = tipoContaMapper;
    }

    public Conta toEntity(ContaRequest request, Usuario usuario) {
        Conta conta = new Conta();
        conta.setNome(request.nome());
        conta.setSaldo(request.saldo());
        conta.setUsuario(usuario);


        if (request.tipoId() != null) {
            TipoContaUsuario tipo = tipoRepo.findById(request.tipoId())
                    .orElseThrow(() -> new RuntimeException("Tipo de conta não encontrado com o ID: " + request.tipoId()));

            conta.setTipo(tipo);
        } else {

            throw new IllegalArgumentException("O campo 'tipoId' é obrigatório.");
        }


        return conta;
    }

    public ContaResponse toResponse(Conta conta) {
        return new ContaResponse(
                conta.getId(),
                conta.getNome(),
                conta.getSaldo(),

                conta.getTipo() != null ? tipoContaMapper.toResponse(conta.getTipo()) : null
        );
    }
}