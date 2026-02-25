package com.vcontrola.vcontrola.service;

import com.vcontrola.vcontrola.controller.request.ItemPlanejamentoRequest;
import com.vcontrola.vcontrola.controller.request.TransacaoRequest;
import com.vcontrola.vcontrola.entity.*;
import com.vcontrola.vcontrola.enums.StatusPlanejamento;
import com.vcontrola.vcontrola.enums.TipoTransacao;
import com.vcontrola.vcontrola.infra.exception.RegraDeNegocioException;
import com.vcontrola.vcontrola.mapper.FinanceiroMapper;
import com.vcontrola.vcontrola.repository.ContaRepository;
import com.vcontrola.vcontrola.repository.ControleFinanceiroRepository;
import com.vcontrola.vcontrola.repository.ItemPlanejamentoRepository;
import com.vcontrola.vcontrola.repository.TipoContaUsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FinanceiroServiceTest {

    @Mock
    private ControleFinanceiroRepository controleRepo;

    @Mock
    private ItemPlanejamentoRepository itemRepo;

    @Mock
    private ContaRepository contaRepo;

    @Mock
    private TipoContaUsuarioRepository tipoContaUsuarioRepo;

    @Mock
    private TransacaoService transacaoService;

    @Mock private FinanceiroMapper mapper;

    @InjectMocks
    private FinanceiroService financeiroService;



    private UUID usuarioId;
    private Usuario usuario;
    private UUID contaId;
    private Conta conta;
    private UUID carteiraId;
    private TipoContaUsuario carteira;
    private UUID itemId;

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

        carteiraId = UUID.randomUUID();
        carteira = new TipoContaUsuario();
        carteira.setId(carteiraId);

        itemId = UUID.randomUUID();

    }

    @Test
    @DisplayName("Deve somar o valor ao saldo disponível e salvar o controle financeiro")
    void adicionarSaldoVirtual_DeveAdicionarSaldoComSucesso() {


        ControleFinanceiro controleMock = new ControleFinanceiro();
        controleMock.setId(UUID.randomUUID());
        controleMock.setUsuario(usuario);
        controleMock.setSaldoDisponivel(new BigDecimal("100.00")); // Saldo inicial: R$ 100

        BigDecimal valorParaAdicionar = new BigDecimal("50.00"); // Vamos adicionar R$ 50
        when(controleRepo.findByUsuarioId(usuario.getId())).thenReturn(Optional.of(controleMock));


        // 2. ACT (AÇÃO)

        financeiroService.adicionarSaldoVirtual(valorParaAdicionar, usuario);
        // 3. ASSERT
        ArgumentCaptor<ControleFinanceiro> captor = ArgumentCaptor.forClass(ControleFinanceiro.class);
        verify(controleRepo, times(1)).save(captor.capture());
        ControleFinanceiro controleSalvo = captor.getValue();
        assertEquals(new BigDecimal("150.00"), controleSalvo.getSaldoDisponivel());
        assertEquals(usuario.getId(), controleSalvo.getUsuario().getId());
    }

    @Test
    @DisplayName("Deve criar um item de planejamento com sucesso quando todos os dados forem válidos")
    void criarItem_ComSucesso() {
        // 1. ARRANGE
        usuario.setId(usuarioId);

        ItemPlanejamentoRequest request = new ItemPlanejamentoRequest(
                carteiraId, new BigDecimal("1000.00"), contaId);

        ControleFinanceiro controleMock = new ControleFinanceiro();
        Conta contaMock = new Conta();
        TipoContaUsuario carteiraMock = new TipoContaUsuario();
        ItemPlanejamento itemMapeado = new ItemPlanejamento();


        when(controleRepo.findByUsuarioId(usuarioId)).thenReturn(Optional.of(controleMock));
        when(contaRepo.findById(contaId)).thenReturn(Optional.of(contaMock));
        when(tipoContaUsuarioRepo.findById(carteiraId)).thenReturn(Optional.of(carteiraMock));

        when(mapper.toEntity(request, contaMock, carteiraMock, controleMock)).thenReturn(itemMapeado);

        // 2. ACT

        financeiroService.criarItem(request, usuario);

        // 3. ASSERT

        ArgumentCaptor<ItemPlanejamento> captor = ArgumentCaptor.forClass(ItemPlanejamento.class);
        verify(itemRepo, times(1)).save(captor.capture());
        assertEquals(itemMapeado, captor.getValue());

    }
    @Test
    @DisplayName("Deve lançar exceção quando a conta destino não for encontrada")
    void criarItem_QuandoContaNaoEncontrada_DeveLancarExcecao() {
        // Arrange
        ItemPlanejamentoRequest request = new ItemPlanejamentoRequest(
                carteiraId, new BigDecimal("1000.00"), contaId);

        ControleFinanceiro controleMock = new ControleFinanceiro();
        when(controleRepo.findByUsuarioId(usuarioId)).thenReturn(Optional.of(controleMock));


        when(contaRepo.findById(contaId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> financeiroService.criarItem(request, usuario));

        assertEquals("Conta destino não encontrada", exception.getMessage());
        verifyNoInteractions(tipoContaUsuarioRepo, mapper, itemRepo);
    }

    @Test
    @DisplayName("Deve atualizar o item com sucesso quando todos os dados e regras forem válidos")
    void atualizarItem_ComSucesso() {
        // Arrange
        Usuario usuarioDono = new Usuario();
        usuarioDono.setId(usuarioId);

        ControleFinanceiro controle = new ControleFinanceiro();
        controle.setUsuario(usuarioDono);

        ItemPlanejamento itemExistente = new ItemPlanejamento();
        itemExistente.setId(itemId);
        itemExistente.setControle(controle);
        itemExistente.setStatus(StatusPlanejamento.PENDENTE);

        ItemPlanejamentoRequest request = new ItemPlanejamentoRequest(
                carteiraId, new BigDecimal("500.00"), contaId);

        Conta novaConta = new Conta();
        TipoContaUsuario novaCarteira = new TipoContaUsuario();

        when(itemRepo.findById(itemId)).thenReturn(Optional.of(itemExistente));
        when(contaRepo.findById(contaId)).thenReturn(Optional.of(novaConta));
        when(tipoContaUsuarioRepo.findById(carteiraId)).thenReturn(Optional.of(novaCarteira));

        // Act
        financeiroService.atualizarItem(itemId, request, usuarioDono);

        // Assert
        ArgumentCaptor<ItemPlanejamento> captor = ArgumentCaptor.forClass(ItemPlanejamento.class);
        verify(itemRepo).save(captor.capture());

        ItemPlanejamento itemSalvo = captor.getValue();
        assertEquals(new BigDecimal("500.00"), itemSalvo.getValor());
        assertEquals(novaConta, itemSalvo.getContaDestino());
        assertEquals(novaCarteira, itemSalvo.getCarteira());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar um item inexistente")
    void atualizarItem_QuandoItemNaoEncontrado_DeveLancarExcecao() {
        ItemPlanejamentoRequest request = new ItemPlanejamentoRequest(
                carteiraId, new BigDecimal("500.00"), contaId);


        when(itemRepo.findById(itemId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> financeiroService.atualizarItem(itemId, request, usuario));

        assertEquals("Item não encontrado", exception.getMessage());
        verifyNoInteractions(contaRepo, tipoContaUsuarioRepo);
        verify(itemRepo, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar o item de outro usuário (Acesso Negado)")
    void atualizarItem_QuandoUsuarioDiferente_DeveLancarExcecao() {
        ItemPlanejamentoRequest request = new ItemPlanejamentoRequest(
                carteiraId, new BigDecimal("500.00"), contaId);

        Usuario hacker = new Usuario();
        hacker.setId(UUID.randomUUID());

        Usuario donoReal = new Usuario();
        donoReal.setId(UUID.randomUUID());

        ControleFinanceiro controle = new ControleFinanceiro();
        controle.setUsuario(donoReal);

        ItemPlanejamento item = new ItemPlanejamento();
        item.setControle(controle);

        when(itemRepo.findById(itemId)).thenReturn(Optional.of(item));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> financeiroService.atualizarItem(itemId, request, hacker));

        assertEquals("Acesso negado", exception.getMessage());
        verify(itemRepo, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar um item que está com status GUARDADO (cadeado)")
    void atualizarItem_QuandoStatusGuardado_DeveLancarExcecao() {
        ItemPlanejamentoRequest request = new ItemPlanejamentoRequest(
                carteiraId, new BigDecimal("500.00"), contaId);

        Usuario dono = new Usuario();
        dono.setId(usuarioId);

        ControleFinanceiro controle = new ControleFinanceiro();
        controle.setUsuario(dono);

        ItemPlanejamento item = new ItemPlanejamento();
        item.setControle(controle);
        item.setStatus(StatusPlanejamento.GUARDADO);

        when(itemRepo.findById(itemId)).thenReturn(Optional.of(item));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> financeiroService.atualizarItem(itemId, request, dono));

        assertEquals("Desbloqueie o item (cadeado) antes de editar.", exception.getMessage());
        verify(itemRepo, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando a nova conta destino não for encontrada")
    void atualizarItem_QuandoContaNaoEncontrada_DeveLancarExcecao() {
        ItemPlanejamentoRequest request = new ItemPlanejamentoRequest(
                carteiraId, new BigDecimal("500.00"), contaId);

        Usuario dono = new Usuario();
        dono.setId(usuarioId);
        ControleFinanceiro controle = new ControleFinanceiro();
        controle.setUsuario(dono);
        ItemPlanejamento item = new ItemPlanejamento();
        item.setControle(controle);
        item.setStatus(StatusPlanejamento.PENDENTE);

        when(itemRepo.findById(itemId)).thenReturn(Optional.of(item));

        when(contaRepo.findById(contaId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> financeiroService.atualizarItem(itemId, request, dono));

        assertEquals("Conta destino não encontrada", exception.getMessage());
        verifyNoInteractions(tipoContaUsuarioRepo);
        verify(itemRepo, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando a nova carteira não for encontrada")
    void atualizarItem_QuandoCarteiraNaoEncontrada_DeveLancarExcecao() {
        ItemPlanejamentoRequest request = new ItemPlanejamentoRequest(
                carteiraId, new BigDecimal("500.00"), contaId);


        Usuario dono = new Usuario();
        dono.setId(usuarioId);
        ControleFinanceiro controle = new ControleFinanceiro();
        controle.setUsuario(dono);
        ItemPlanejamento item = new ItemPlanejamento();
        item.setControle(controle);
        item.setStatus(StatusPlanejamento.PENDENTE);

        when(itemRepo.findById(itemId)).thenReturn(Optional.of(item));
        when(contaRepo.findById(contaId)).thenReturn(Optional.of(new Conta()));

        when(tipoContaUsuarioRepo.findById(carteiraId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> financeiroService.atualizarItem(itemId, request, dono));

        assertEquals("Carteira não encontrada", exception.getMessage());
        verify(itemRepo, never()).save(any());
    }

    @Test
    @DisplayName("Deve excluir o item com sucesso quando todas as validações passarem")
    void excluirItem_ComSucesso() {
        // Arrange
        ControleFinanceiro controle = new ControleFinanceiro();
        controle.setUsuario(usuario);

        ItemPlanejamento item = new ItemPlanejamento();
        item.setId(itemId);
        item.setControle(controle);
        item.setStatus(StatusPlanejamento.PENDENTE);

        when(itemRepo.findById(itemId)).thenReturn(Optional.of(item));

        // Act
        financeiroService.excluirItem(itemId, usuario);

        // Assert
        verify(itemRepo, times(1)).delete(item);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar excluir um item com status GUARDADO (cadeado fechado)")
    void excluirItem_QuandoStatusGuardado_DeveLancarExcecao() {
        // Arrange
        ControleFinanceiro controle = new ControleFinanceiro();
        controle.setUsuario(usuario);

        ItemPlanejamento item = new ItemPlanejamento();
        item.setId(itemId);
        item.setControle(controle);
        item.setStatus(StatusPlanejamento.GUARDADO);

        when(itemRepo.findById(itemId)).thenReturn(Optional.of(item));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> financeiroService.excluirItem(itemId, usuario));

        assertEquals("Desbloqueie o item (cadeado) antes de excluir para estornar o valor.", exception.getMessage());
        verify(itemRepo, never()).delete(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar excluir um item de outro usuário")
    void excluirItem_QuandoUsuarioDiferente_DeveLancarExcecao() {
        // Arrange
        Usuario outroUsuario = new Usuario();
        outroUsuario.setId(UUID.randomUUID());

        ControleFinanceiro controleOutroUsuario = new ControleFinanceiro();
        controleOutroUsuario.setUsuario(outroUsuario);

        ItemPlanejamento item = new ItemPlanejamento();
        item.setId(itemId);
        item.setControle(controleOutroUsuario);

        when(itemRepo.findById(itemId)).thenReturn(Optional.of(item));


        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> financeiroService.excluirItem(itemId, usuario));

        assertEquals("Acesso negado", exception.getMessage());
        verify(itemRepo, never()).delete(any());
    }





    @Test
    @DisplayName("Deve realizar resgate parcial e manter o status GUARDADO se sobrar saldo no item")
    void resgatarParcial_QuandoSobrarSaldo_ComSucesso() {
        // Arrange
        ControleFinanceiro controle = new ControleFinanceiro();
        controle.setUsuario(usuario);
        carteira.setNome("Férias");

        ItemPlanejamento item = new ItemPlanejamento();
        item.setId(itemId);
        item.setControle(controle);
        item.setStatus(StatusPlanejamento.GUARDADO);
        item.setValor(new BigDecimal("500.00"));
        item.setContaDestino(conta);
        item.setCarteira(carteira);

        when(itemRepo.findById(itemId)).thenReturn(Optional.of(item));

        BigDecimal valorResgate = new BigDecimal("200.00");

        // Act
        financeiroService.resgatarParcial(itemId, valorResgate, usuario);

        // Assert
        assertEquals(new BigDecimal("300.00"), item.getValor());


        assertEquals(StatusPlanejamento.GUARDADO, item.getStatus());
        ArgumentCaptor<TransacaoRequest> captor = ArgumentCaptor.forClass(TransacaoRequest.class);
        verify(transacaoService).criar(captor.capture(), eq(usuario));
        TransacaoRequest transacao = captor.getValue();
        assertEquals("Resgate Parcial: Férias", transacao.descricao());
        assertEquals(new BigDecimal("200.00"), transacao.valor());
        assertEquals(TipoTransacao.GASTOS, transacao.tipo());

        verify(itemRepo).save(item);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar resgatar de um item que NÃO está com status GUARDADO")
    void resgatarParcial_QuandoStatusNaoForGuardado_DeveLancarExcecao() {
        ControleFinanceiro controle = new ControleFinanceiro();
        controle.setUsuario(usuario);

        ItemPlanejamento item = new ItemPlanejamento();
        item.setId(itemId);
        item.setControle(controle);
        item.setStatus(StatusPlanejamento.PENDENTE);

        when(itemRepo.findById(itemId)).thenReturn(Optional.of(item));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> financeiroService.resgatarParcial(itemId, new BigDecimal("100.00"), usuario));

        assertEquals("Apenas itens guardados podem ter resgate parcial.", exception.getMessage());
        verifyNoInteractions(transacaoService);
    }
}