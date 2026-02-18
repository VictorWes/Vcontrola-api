package com.vcontrola.vcontrola.service;

import com.vcontrola.vcontrola.controller.request.CartaoRequest;
import com.vcontrola.vcontrola.controller.response.CartaoResponse;
import com.vcontrola.vcontrola.entity.CartaoCredito;
import com.vcontrola.vcontrola.entity.Usuario;
import com.vcontrola.vcontrola.mapper.CartaoCreditoMapper;
import com.vcontrola.vcontrola.repository.CartaoCreditoRepository;
import com.vcontrola.vcontrola.repository.ParcelaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import static org.mockito.Mockito.verify;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class CartaoCreditoServiceTest {
    @Mock
    private CartaoCreditoRepository repository;

    @Mock
    private ParcelaRepository parcelaRepository;

    @Mock
    private CartaoCreditoMapper mapper;

    @InjectMocks
    private CartaoCreditoService cartaoCreditoService;

    private UUID usuarioId;
    private UUID cartaoId;
    private Usuario usuarioMock;
    private CartaoCredito cartaoMock;

    @BeforeEach
    void setUp(){
        usuarioId = UUID.randomUUID();
        cartaoId = UUID.randomUUID();

        usuarioMock = new Usuario();
        usuarioMock.setId(usuarioId);

        cartaoMock = new CartaoCredito();
        cartaoMock.setId(cartaoId);
    }

    @Test
    @DisplayName("Deve listar os cartões do usuário com o valor da fatura atual calculado")
    void listar_DeveRetornarListaDeCartoesComFaturaAtual() {

        // ARRANGE

        cartaoMock.setId(cartaoId);

        when(repository.findByUsuario(usuarioMock)).thenReturn(List.of(cartaoMock));

        CartaoResponse mapperResponseMock = new CartaoResponse(
                cartaoId, // Usando o UUID aqui
                "Nubank",
                BigDecimal.valueOf(5000),
                BigDecimal.valueOf(5000),
                10,
                3,
                BigDecimal.ZERO
        );
        when(mapper.toResponse(cartaoMock)).thenReturn(mapperResponseMock);


        when(parcelaRepository.somarFaturaAtual(eq(cartaoId), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(new BigDecimal("150.50"));


        // ACT
        List<CartaoResponse> resultado = cartaoCreditoService.listar(usuarioMock);

        // ASSERT
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        CartaoResponse responseFinal = resultado.get(0);

        assertEquals(cartaoId, responseFinal.id());
        assertEquals(new BigDecimal("150.50"), responseFinal.valorFaturaAtual());


        verify(repository, times(1)).findByUsuario(usuarioMock);
        verify(parcelaRepository, times(1)).somarFaturaAtual(eq(cartaoId), any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando o usuário não tiver cartões de crédito")
    void listar_QuandoNaoHaCartoes_DeveRetornarListaVazia() {
        // ARRANGE
        when(repository.findByUsuario(usuarioMock)).thenReturn(List.of());

        // ACT
        List<CartaoResponse> resultado = cartaoCreditoService.listar(usuarioMock);

        // ASSERT
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());

        verify(repository, times(1)).findByUsuario(usuarioMock);
        verifyNoInteractions(parcelaRepository, mapper);
    }

    @Test
    @DisplayName("Deve tratar fatura null como zero")
    void listar_QuandoFaturaAtualForNull_DeveTratarComoZero(){
        //ARRANGE
        cartaoMock.setId(cartaoId);

        when(repository.findByUsuario(usuarioMock)).thenReturn(List.of(cartaoMock));

        CartaoResponse mapperResponseMock = new CartaoResponse(
                cartaoId,
                "Nubank",
                BigDecimal.valueOf(5000),
                BigDecimal.valueOf(5000),
                10,
                3,
                null
        );
        when(mapper.toResponse(cartaoMock)).thenReturn(mapperResponseMock);

        when(parcelaRepository.somarFaturaAtual(eq(cartaoId), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(null);

        // ACT
        List<CartaoResponse> resultado = cartaoCreditoService.listar(usuarioMock);

        // ASSERT
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        CartaoResponse responseFinal = resultado.get(0);
        assertEquals(cartaoId, responseFinal.id());
        assertEquals(BigDecimal.ZERO, responseFinal.valorFaturaAtual());
        verify(repository, times(1)).findByUsuario(usuarioMock);
        verify(parcelaRepository, times(1)).somarFaturaAtual(eq(cartaoId), any(LocalDate.class), any(LocalDate.class));

    }

    @Test
    @DisplayName("Deve listar todos os cartões quando o utilizador possui múltiplos registos")
    void listar_QuandoUsuarioPossuiVariosCartoes_DeveRetornarListaCompleta() {

        // ARRANGE (PREPARAÇÃO)
        UUID cartaoId2 = UUID.randomUUID();
        CartaoCredito cartaoMock2 = new CartaoCredito();
        cartaoMock2.setId(cartaoId2);
        cartaoMock2.setUsuario(usuarioMock);


        when(repository.findByUsuario(usuarioMock)).thenReturn(List.of(cartaoMock, cartaoMock2));

        CartaoResponse responseMock1 = new CartaoResponse(
                cartaoId, "Nubank", BigDecimal.valueOf(5000), BigDecimal.valueOf(5000), 10, 3, BigDecimal.ZERO
        );
        when(mapper.toResponse(cartaoMock)).thenReturn(responseMock1);

        CartaoResponse responseMock2 = new CartaoResponse(
                cartaoId2, "Itaú", BigDecimal.valueOf(3000), BigDecimal.valueOf(3000), 15, 8, BigDecimal.ZERO
        );
        when(mapper.toResponse(cartaoMock2)).thenReturn(responseMock2);

        when(parcelaRepository.somarFaturaAtual(eq(cartaoId), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(new BigDecimal("150.00"));

        when(parcelaRepository.somarFaturaAtual(eq(cartaoId2), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(new BigDecimal("300.00"));


        // ACT (AÇÃO)
        List<CartaoResponse> resultado = cartaoCreditoService.listar(usuarioMock);


        // ASSERT (VERIFICAÇÃO)

        assertNotNull(resultado);
        assertEquals(2, resultado.size());

        CartaoResponse cartao1 = resultado.get(0);
        assertEquals(cartaoId, cartao1.id());
        assertEquals(new BigDecimal("150.00"), cartao1.valorFaturaAtual());


        CartaoResponse cartao2 = resultado.get(1);
        assertEquals(cartaoId2, cartao2.id());
        assertEquals(new BigDecimal("300.00"), cartao2.valorFaturaAtual());

        verify(repository, times(1)).findByUsuario(usuarioMock);
        verify(mapper, times(2)).toResponse(any(CartaoCredito.class));
        verify(parcelaRepository, times(2)).somarFaturaAtual(any(), any(), any());
    }

    @Test
    @DisplayName("Deve criar um cartão de crédito para o usuário")
    void criar_DeveSalvarCartaoCredito() {
        // ARRANGE

        CartaoRequest requestMock = new CartaoRequest(
                "Nubank",
                BigDecimal.valueOf(5000),
                10,
                3
        );

        when(mapper.toEntity(requestMock, usuarioMock)).thenReturn(cartaoMock);

        // ACT
        cartaoCreditoService.criar(requestMock, usuarioMock);

        // ASSERT
        verify(mapper, times(1)).toEntity(requestMock, usuarioMock);
        verify(repository, times(1)).save(cartaoMock);

    }

    @Test
    @DisplayName("Deve lançar exceção quando ocorrer erro ao salvar no banco (ex: restrição única)")
    void criar_QuandoErroNoBanco_DeveLancarExcecao() {
        // ARRANGE
        CartaoRequest requestMock = new CartaoRequest("Nubank", BigDecimal.valueOf(5000), 10, 3);
        when(mapper.toEntity(requestMock, usuarioMock)).thenReturn(cartaoMock);

        when(repository.save(cartaoMock)).thenThrow(new DataIntegrityViolationException("Erro de restrição"));

        // ACT & ASSERT
        assertThrows(DataIntegrityViolationException.class, () -> {
            cartaoCreditoService.criar(requestMock, usuarioMock);
        });


        verify(mapper, times(1)).toEntity(requestMock, usuarioMock);
        verify(repository, times(1)).save(cartaoMock);
    }

    @Test
    @DisplayName("Não deve chamar o repositório se ocorrer erro no mapeamento")
    void criar_QuandoMapperFalhar_NaoDeveSalvarNoBanco() {
        // ARRANGE
        CartaoRequest requestNulo = null;


        when(mapper.toEntity(requestNulo, usuarioMock)).thenThrow(new IllegalArgumentException("Request nulo"));

        // ACT & ASSERT
        assertThrows(IllegalArgumentException.class, () -> {
            cartaoCreditoService.criar(requestNulo, usuarioMock);
        });


        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Deve atualizar um cartão de crédito existente para o usuário")
    void atualizar_DeveAtualizarCartaoCredito() {
        //ARRANGE
        CartaoRequest requestMock = new CartaoRequest(
                "Nubank",
                BigDecimal.valueOf(6000),
                10,
                3
        );

        cartaoMock.setUsuario(usuarioMock);
        cartaoMock.setLimiteTotal(BigDecimal.valueOf(5000));
        cartaoMock.setLimiteDisponivel(BigDecimal.valueOf(5000));

        when(repository.findById(cartaoId)).thenReturn(java.util.Optional.of(cartaoMock));

        // ACT
        cartaoCreditoService.atualizar(cartaoId, requestMock, usuarioMock);

        // ASSERT
        verify(repository, times(1)).findById(cartaoId);
        verify(repository, times(1)).save(cartaoMock);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar para um valor negativo")
    void atualizar_QuandoLimiteNegativo_DeveLancarExcecao() {
        // ARRANGE
        CartaoRequest requestMock = new CartaoRequest(
                "Nubank",
                BigDecimal.valueOf(-1000), // Limite negativo
                10,
                3
        );

        cartaoMock.setUsuario(usuarioMock);

        cartaoMock.setLimiteTotal(BigDecimal.valueOf(5000));
        cartaoMock.setLimiteDisponivel(BigDecimal.valueOf(5000));

        when(repository.findById(cartaoId)).thenReturn(java.util.Optional.of(cartaoMock));

        // ACT & ASSERT
        assertThrows(IllegalArgumentException.class, () -> {
            cartaoCreditoService.atualizar(cartaoId, requestMock, usuarioMock);
        });

        verify(repository, times(1)).findById(cartaoId);

    }

    @Test
    @DisplayName("Deve excluir um cartão de credito existente para o usuário")
    void excluir_DeveExcluirCartãoDeCredito(){
        // ARRANGE
        cartaoMock.setUsuario(usuarioMock);

        when(repository.findById(cartaoId)).thenReturn(java.util.Optional.of(cartaoMock));

        // ACT
        cartaoCreditoService.excluir(cartaoId, usuarioMock);

        // ASSERT
        verify(repository, times(1)).findById(cartaoId);
        verify(repository, times(1)).delete(cartaoMock);


    }
}