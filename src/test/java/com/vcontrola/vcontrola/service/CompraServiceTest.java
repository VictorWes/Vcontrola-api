package com.vcontrola.vcontrola.service;

import com.vcontrola.vcontrola.controller.request.CompraRequest;
import com.vcontrola.vcontrola.controller.response.CompraResponse;
import com.vcontrola.vcontrola.entity.CartaoCredito;
import com.vcontrola.vcontrola.entity.Compra;
import com.vcontrola.vcontrola.entity.Conta;
import com.vcontrola.vcontrola.entity.Parcela;
import com.vcontrola.vcontrola.mapper.CompraMapper;
import com.vcontrola.vcontrola.repository.CartaoCreditoRepository;
import com.vcontrola.vcontrola.repository.CompraRepository;
import com.vcontrola.vcontrola.repository.ContaRepository;
import com.vcontrola.vcontrola.repository.ParcelaRepository;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.eq;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CompraServiceTest {

    @Mock
    private CompraRepository compraRepository;

    @Mock
    private CartaoCreditoRepository cartaoRepository;

    @Mock
    private CompraMapper mapper;

    @Mock
    private ParcelaRepository parcelaRepository;

    @Mock
    private ContaRepository contaRepository;

    @InjectMocks
    private CompraService compraService;

    private UUID cartaoId;
    private CartaoCredito cartaoMock;
    private UUID compraId;
    private Compra compraMock;

    @BeforeEach
    void setUp() {
        cartaoId = UUID.randomUUID();
        compraId = UUID.randomUUID();

        cartaoMock = new CartaoCredito();
        cartaoMock.setId(cartaoId);

        compraMock = new Compra();
        compraMock.setId(compraId);
        compraMock.setCartao(cartaoMock);
    }

    @Test
    @DisplayName("criar deve retornar sucesso quando os dados forem válidos")
    void criar_QuandoEnviandoTodosOsDados_DeveSalvarComSucesso() {

        //Arrange
        CompraRequest request = new CompraRequest(
               "Compra Teste",
                new java.math.BigDecimal("100.00"),
                2,
                java.time.LocalDate.now(),
                cartaoId
        );

        cartaoMock.setLimiteDisponivel(BigDecimal.valueOf(1000.00));
        cartaoMock.setLimiteTotal(BigDecimal.valueOf(1000.00));

        cartaoMock.setDiaVencimento(10);
        cartaoMock.setDiaFechamento(3);

        when(cartaoRepository.findById(eq(cartaoId))).thenReturn(java.util.Optional.of(cartaoMock));
        when(mapper.toEntity(eq(request), eq(cartaoMock))).thenReturn(compraMock);
        when(compraRepository.save(eq(compraMock))).thenReturn(compraMock);
        //Act
        assertDoesNotThrow(() -> compraService.criar(request));
        //Assert
        verify(cartaoRepository, times(1)).findById(eq(cartaoId));
        verify(mapper, times(1)).toEntity(eq(request), eq(cartaoMock));
        verify(compraRepository, times(1)).save(eq(compraMock)

        );
    }

    @Test
    @DisplayName("Deve gerar as parcelas com valores e datas corretas quando a compra for após o dia de fechamento")
    void criar_DeveGerarParcelasCorretamente_QuandoCompraAposFechamento() {


        cartaoMock.setId(cartaoId);
        cartaoMock.setLimiteTotal(new BigDecimal("1000.00"));
        cartaoMock.setLimiteDisponivel(new BigDecimal("1000.00"));
        cartaoMock.setDiaFechamento(3);
        cartaoMock.setDiaVencimento(10);

        LocalDate dataDaCompra = LocalDate.of(2024, 5, 5);
        CompraRequest request = new CompraRequest(
                "Teclado Mecânico", new BigDecimal("100.00"), 2, dataDaCompra, cartaoId
        );

        Compra compraMock = new Compra(); // O objeto que o mapper vai devolver

        when(cartaoRepository.findById(cartaoId)).thenReturn(java.util.Optional.of(cartaoMock));
        when(mapper.toEntity(request, cartaoMock)).thenReturn(compraMock);

        // 2. ACT
        compraService.criar(request);
        ArgumentCaptor<Compra> compraCaptor = ArgumentCaptor.forClass(Compra.class);
        verify(compraRepository).save(compraCaptor.capture());

        Compra compraSalva = compraCaptor.getValue();
        List<Parcela> parcelasGeradas = compraSalva.getParcelas();

        assertEquals(2, parcelasGeradas.size());

        Parcela parcela1 = parcelasGeradas.get(0);
        Parcela parcela2 = parcelasGeradas.get(1);

        assertEquals(new BigDecimal("50.00"), parcela1.getValorParcela());
        assertEquals(new BigDecimal("50.00"), parcela2.getValorParcela());

        assertEquals(LocalDate.of(2024, 6, 10), parcela1.getDataVencimento());
        assertEquals(LocalDate.of(2024, 7, 10), parcela2.getDataVencimento());

        assertEquals(new BigDecimal("900.00"), cartaoMock.getLimiteDisponivel());
    }

    @Test
    @DisplayName("Deve retornar página de compras com o status de pagamento correto")
    void listarPorCartao_DeveRetornarPaginaDeCompras() {

        // 1. ARRANGE (PREPARAÇÃO)
        Pageable pageable = PageRequest.of(0, 10);


        Compra compraPendente = new Compra();
        compraPendente.setId(UUID.randomUUID());

        Compra compraMapeada = new Compra();
        compraMapeada.setId(UUID.randomUUID());

        List<Compra> listaCompras = List.of(compraPendente, compraMapeada);
        Page<Compra> paginaDeCompras = new PageImpl<>(listaCompras, pageable, listaCompras.size());


        when(compraRepository.findByCartaoId(cartaoId, pageable)).thenReturn(paginaDeCompras);


        when(parcelaRepository.existsByCompraIdAndPagaFalse(compraPendente.getId())).thenReturn(true);
        CompraResponse response1 = new CompraResponse(compraPendente.getId(), "Geladeira", new BigDecimal("3000"), 2, new BigDecimal("1500"), LocalDate.now(), false);
        when(mapper.toResponse(compraPendente, false)).thenReturn(response1); // Note o 'false' aqui!

        when(parcelaRepository.existsByCompraIdAndPagaFalse(compraMapeada.getId())).thenReturn(false);
        CompraResponse response2 = new CompraResponse(compraMapeada.getId(), "TV", new BigDecimal("2000"), 2, new BigDecimal("1000"), LocalDate.now(), true);
        when(mapper.toResponse(compraMapeada, true)).thenReturn(response2); // Note o 'true' aqui!


        // 2. ACT (AÇÃO)

        Page<CompraResponse> resultado = compraService.listarPorCartao(cartaoId, pageable);

        // 3. ASSERT (VERIFICAÇÃO)
        assertNotNull(resultado);
        assertEquals(2, resultado.getTotalElements());

        List<CompraResponse> conteudo = resultado.getContent();
        assertEquals("Geladeira", conteudo.get(0).nome());
        assertEquals("TV", conteudo.get(1).nome());


        verify(compraRepository, times(1)).findByCartaoId(cartaoId, pageable);


        verify(parcelaRepository, times(1)).existsByCompraIdAndPagaFalse(compraPendente.getId());
        verify(parcelaRepository, times(1)).existsByCompraIdAndPagaFalse(compraMapeada.getId());

        // Garante que o mapper foi chamado invertendo a lógica corretamente
        verify(mapper, times(1)).toResponse(compraPendente, false);
        verify(mapper, times(1)).toResponse(compraMapeada, true);
    }

    @Test
    @DisplayName("Deve atualizar uma compra quando os dados forem válidos e não houver parcelas pagas")
    void editar_DeveAtualizarCompra(){
        // Arrange
        CompraRequest request = new CompraRequest(
                "Compra Editada",
                new BigDecimal("150.00"),
                3,
                LocalDate.now(),
                cartaoId
        );

        Parcela parcelaPaga = new Parcela();
        parcelaPaga.setPaga(false);
        parcelaPaga.setNumeroParcela(3);

        Parcela parcelaPendente = new Parcela();
        parcelaPendente.setPaga(false);

        compraMock.setValorTotal(new BigDecimal("150.00"));
        compraMock.setParcelas(List.of(parcelaPaga, parcelaPendente));
        cartaoMock.setLimiteDisponivel(new BigDecimal("850.00"));
        cartaoMock.setDiaFechamento(5);
        cartaoMock.setDiaVencimento(22);

        compraMock.setQtdeParcelas(2);

        when(compraRepository.findById(compraId)).thenReturn(java.util.Optional.of(compraMock));

        // Act
        compraService.editar(compraId, request);

        // Assert
        ArgumentCaptor<Compra> compraCaptor = ArgumentCaptor.forClass(Compra.class);
        verify(compraRepository).save(compraCaptor.capture());
        Compra compraSalva = compraCaptor.getValue();
        assertEquals("Compra Editada", compraSalva.getNome());
        assertEquals(new BigDecimal("150.00"), compraSalva.getValorTotal());

    }

    @Test
    @DisplayName("Deve excluir uma compra e suas parcelas associadas. Valor pago deve ser estornado para a conta")
    void excluir_DeveExcluirCompraEParcelas() {
        // ARRANGE

        UUID contaId = UUID.randomUUID();

        Conta contaMock = new Conta();
        contaMock.setId(contaId);
        contaMock.setSaldo(BigDecimal.ZERO);

        cartaoMock.setLimiteDisponivel(new BigDecimal("1000.00"));
        cartaoMock.setLimiteTotal(new BigDecimal("1000.00"));

        Parcela parcela1 = new Parcela();
        parcela1.setPaga(true);
        parcela1.setValorParcela(new BigDecimal("50.00"));

        Parcela parcela2 = new Parcela();
        parcela2.setPaga(false);
        parcela2.setValorParcela(new BigDecimal("50.00"));

        compraMock.setCartao(cartaoMock);
        compraMock.setValorTotal(new BigDecimal("100.00"));
        compraMock.setQtdeParcelas(2);
        compraMock.setParcelas(List.of(parcela1, parcela2));

        when(compraRepository.findById(compraId)).thenReturn(java.util.Optional.of(compraMock));
        when(contaRepository.findById(contaId)).thenReturn(java.util.Optional.of(contaMock));

        // ACT
        compraService.excluir(compraId, contaId);

        // ASSERT
        verify(compraRepository, times(1)).findById(compraId);
        verify(contaRepository, times(1)).findById(contaId);

        verify(contaRepository, times(1)).save(contaMock);
        verify(cartaoRepository, times(1)).save(cartaoMock);
        verify(compraRepository, times(1)).delete(compraMock);

        assertEquals(new BigDecimal("1000.00"), cartaoMock.getLimiteDisponivel());
        assertEquals(new BigDecimal("50.00"), contaMock.getSaldo());
    }


}