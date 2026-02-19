package com.vcontrola.vcontrola.service;

import com.vcontrola.vcontrola.controller.request.ContaRequest;
import com.vcontrola.vcontrola.entity.Conta;
import com.vcontrola.vcontrola.entity.Usuario;
import com.vcontrola.vcontrola.mapper.ContaMapper;
import com.vcontrola.vcontrola.repository.ContaRepository;
import com.vcontrola.vcontrola.repository.ItemPlanejamentoRepository;
import com.vcontrola.vcontrola.repository.TipoContaUsuarioRepository;
import com.vcontrola.vcontrola.repository.TransacaoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContaServiceTest {

    @Mock
    private ContaRepository repository;

    @Mock
    private TransacaoRepository transacaoRepository;

    @Mock
    private ContaMapper mapper;

    @Mock
    private TipoContaUsuarioRepository tipoRepository;

    @Mock
    private ItemPlanejamentoRepository itemPlanejamentoRepository;

    @InjectMocks
    private ContaService contaService;

    private UUID usuarioId;
    private Usuario usuario;
    private UUID contaId;
    private Conta conta;

    @BeforeEach
    void setUp() {
       usuarioId = UUID.randomUUID();
       usuario = new Usuario();
       usuario.setId(usuarioId);
       usuario.setNome("Victor");
       usuario.setEmail("teste@gmail.com");
       usuario.setTokenRecuperacao(null);

       contaId = UUID.randomUUID();
       conta = new Conta();
       conta.setId(contaId);

       conta.setUsuario(usuario);

    }


    @Test
    @DisplayName("Deve criar uma conta com saldo positivo e associar ao usuário")
    void criarContaComSaldoPositivo() {
        //Arrage

        ContaRequest request = new ContaRequest(
                "Victor",
                new BigDecimal("590.00"),
                contaId
                );

        conta.setNome("Victor");
        conta.setSaldo(new BigDecimal("590.00"));

        when(mapper.toEntity(request, usuario)).thenReturn(conta);

        //Act
        assertDoesNotThrow(() -> contaService.criar(request, usuario));

        //Assert
        ArgumentCaptor<Conta> contaCaptor = ArgumentCaptor.forClass(Conta.class);
        // Verifica se o método save do repositório foi chamado com a conta correta
        verify(repository).save(contaCaptor.capture());
        Conta contaSalva = contaCaptor.getValue();
        assertEquals("Victor", contaSalva.getNome());
        assertEquals(new BigDecimal("590.00"), contaSalva.getSaldo());

    }



}