package com.vcontrola.vcontrola.service;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {
    @Mock
    private JavaMailSender javaMailSender;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailService, "remetente", "nao-responda@vcontrola.com");
    }

    @Test
    @DisplayName("Deve formatar e enviar o e-mail com sucesso")
    void enviarEmailTexto_ComSucesso() {
        // Act
        emailService.enviarEmailTexto("cliente@gmail.com", "Bem-vindo", "Olá, seja bem-vindo!");

        // Assert
        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(javaMailSender, times(1)).send(captor.capture());

        SimpleMailMessage emailEnviado = captor.getValue();


        assertEquals("nao-responda@vcontrola.com", emailEnviado.getFrom());


        assertEquals("cliente@gmail.com", emailEnviado.getTo()[0]);
        assertEquals("Bem-vindo", emailEnviado.getSubject());
        assertEquals("Olá, seja bem-vindo!", emailEnviado.getText());
    }

    @Test
    @DisplayName("Não deve quebrar a aplicação caso o servidor de e-mail falhe (teste do try-catch)")
    void enviarEmailTexto_QuandoServidorFalhar_NaoDeveLancarExcecao() {
        // Arrange

        doThrow(new RuntimeException("Falha de conexão SMTP"))
                .when(javaMailSender).send(any(SimpleMailMessage.class));

        // Act & Assert
        assertDoesNotThrow(() ->
                emailService.enviarEmailTexto("cliente@gmail.com", "Assunto", "Mensagem")
        );

        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
    }
}