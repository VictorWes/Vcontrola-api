package com.vcontrola.vcontrola.service;

import com.vcontrola.vcontrola.entity.Conta;
import com.vcontrola.vcontrola.entity.ControleFinanceiro;
import com.vcontrola.vcontrola.entity.Usuario;
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

    @InjectMocks
    private FinanceiroService financeiroService;

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
    @DisplayName()




}