package com.vcontrola.vcontrola.service;

import com.vcontrola.vcontrola.controller.request.ContaRequest;
import com.vcontrola.vcontrola.controller.response.ContaResponse;
import com.vcontrola.vcontrola.entity.Conta;
import com.vcontrola.vcontrola.entity.Usuario;
import com.vcontrola.vcontrola.infra.exception.GlobalExceptionHandler;
import com.vcontrola.vcontrola.infra.exception.RegraDeNegocioException;
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
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
        //Arrange

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
        verify(repository).save(contaCaptor.capture());
        Conta contaSalva = contaCaptor.getValue();
        assertEquals("Victor", contaSalva.getNome());
        assertEquals(new BigDecimal("590.00"), contaSalva.getSaldo());

    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar criar uma conta com saldo negativo")
    void criarContaComSaldoNegativo() {
        //Arrange
        ContaRequest request = new ContaRequest(
                "Victor",
                new BigDecimal("-100.00"),
                contaId
        );

        //Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> contaService.criar(request, usuario));
        assertEquals("Saldo inicial não pode ser negativo no cadastro.", exception.getMessage());
        verifyNoInteractions(mapper, repository);

    }

    @Test
    @DisplayName("Deve listar as contas associadas a um usuário")
    void listarContasPorUsuario() {
        // 1. Arrange

        ContaResponse responseFalso = new ContaResponse(
                contaId, "Conta Teste", BigDecimal.ZERO, null
        );


        when(repository.findByUsuarioId(usuarioId)).thenReturn(List.of(conta));
        when(mapper.toResponse(conta)).thenReturn(responseFalso);

        // 2. Act
        var contas = contaService.listar(usuarioId);

        // 3. Assert
        assertNotNull(contas);
        assertEquals(1, contas.size());
        assertEquals(contaId, contas.get(0).id());

        verify(repository).findByUsuarioId(usuarioId);
        verify(mapper).toResponse(conta);

    }

    @Test
    @DisplayName("Deve retornar lista vazia quando o usuário não possuir nenhuma conta")
    void listarContasPorUsuario_QuandoListaVazia() {
        // 1. Arrange
        when(repository.findByUsuarioId(usuarioId)).thenReturn(List.of());

        // 2. Act
        var contas = contaService.listar(usuarioId);

        // 3. Assert
        assertNotNull(contas);
        assertTrue(contas.isEmpty());

        verify(repository).findByUsuarioId(usuarioId);


        verifyNoInteractions(mapper);
    }

    @Test
    @DisplayName("Deve atualizar uma conta existente com dados válidos")
    void atualizarContaExistente() {
        // Arrange
        ContaRequest request = new ContaRequest(
                "Conta Atualizada",
                new BigDecimal("1000.00"),
                null
        );
        when(repository.findById(contaId)).thenReturn(java.util.Optional.of(conta));

        // Act
        assertDoesNotThrow(() -> contaService.atualizar(contaId, request, usuario));

        // Assert
        ArgumentCaptor<Conta> contaCaptor = ArgumentCaptor.forClass(Conta.class);
        verify(repository).save(contaCaptor.capture());
        Conta contaAtualizada = contaCaptor.getValue();
        assertEquals("Conta Atualizada", contaAtualizada.getNome());
        assertEquals(new BigDecimal("1000.00"), contaAtualizada.getSaldo());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar uma conta que pertence a outro usuário")
    void atualizar_QuandoContaForDeOutroUsuario_DeveLancarExcecao() {
        // Arrange
        ContaRequest request = new ContaRequest("Hacker", new BigDecimal("9999"), null);

        Usuario usuarioDonoDaConta = new Usuario();
        usuarioDonoDaConta.setId(UUID.randomUUID());
        conta.setUsuario(usuarioDonoDaConta);

        when(repository.findById(contaId)).thenReturn(java.util.Optional.of(conta));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> contaService.atualizar(contaId, request, usuario));

        assertEquals("Você não tem permissão para alterar esta conta.", exception.getMessage());
        verify(repository, never()).save(any()); // Garante que o hacker não salvou nada!
    }

    @Test
    @DisplayName("Deve excluir uma conta existente sem transações associadas")
    void excluirContaExistenteSemTransacoes() {
        // Arrange
        when(repository.findById(contaId)).thenReturn(java.util.Optional.of(conta));
        when(transacaoRepository.existsByContaId(contaId)).thenReturn(false);
        when(itemPlanejamentoRepository.existsByContaDestinoId(contaId)).thenReturn(false);

        // Act
        assertDoesNotThrow(() -> contaService.excluir(contaId, usuario));

        // Assert
        verify(repository).delete(conta);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar excluir uma conta com transações associadas")
    void excluirContaComTransacoesAssociadas() {
        // Arrange
        when(repository.findById(contaId)).thenReturn(java.util.Optional.of(conta));
        when(transacaoRepository.existsByContaId(contaId)).thenReturn(true);

        // Act & Assert
        RegraDeNegocioException exception = assertThrows(RegraDeNegocioException.class,
                () -> contaService.excluir(contaId, usuario));

        assertEquals("Não é possível excluir: Existem transações vinculadas a esta conta. Exclua as transações primeiro.", exception.getMessage());
        verify(repository, never()).delete(any());
    }

}