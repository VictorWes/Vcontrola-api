package com.vcontrola.vcontrola.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${vcontrola.mail.from}")
    private String remetente;

    @Async
    public void enviarEmailTexto(String destinatario, String assunto, String mensagem) {
        try {
            SimpleMailMessage email = new SimpleMailMessage();
            email.setFrom(remetente);
            email.setTo(destinatario);
            email.setSubject(assunto);
            email.setText(mensagem);

            javaMailSender.send(email);
            System.out.println("📧 E-mail enviado com sucesso para: " + destinatario);
        } catch (Exception e) {
            System.err.println("❌ Erro ao enviar e-mail: " + e.getLocalizedMessage());

        }
    }
}
