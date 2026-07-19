package br.com.fiap.agendamento.infrastructure.notification;

import br.com.fiap.agendamento.application.port.NotificacaoService;
import br.com.fiap.agendamento.domain.model.Agendamento;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

/**
 * Implementacao concreta do port NotificacaoService via SMTP (JavaMailSender).
 * Em desenvolvimento/CI o e-mail e capturado pelo MailHog (ver docker-compose.yml);
 * em producao basta trocar as credenciais SMTP nas variaveis de ambiente.
 */
@Component
@Slf4j
public class EmailNotificacaoService implements NotificacaoService {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy 'as' HH:mm");
    private static final String REMETENTE = "no-reply@booking-beleza.com";

    private final JavaMailSender mailSender;

    public EmailNotificacaoService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void enviarConfirmacao(Agendamento agendamento, String emailCliente) {
        enviar(emailCliente, "Agendamento confirmado",
                "Seu agendamento foi confirmado para " + agendamento.getDataHora().format(FORMATO) + ".");
    }

    @Override
    public void enviarCancelamento(Agendamento agendamento, String emailCliente) {
        enviar(emailCliente, "Agendamento cancelado",
                "Seu agendamento de " + agendamento.getDataHora().format(FORMATO) + " foi cancelado.");
    }

    @Override
    public void enviarLembrete(Agendamento agendamento, String emailCliente) {
        enviar(emailCliente, "Lembrete: voce tem um agendamento amanha",
                "Este e um lembrete do seu agendamento em " + agendamento.getDataHora().format(FORMATO) + ".");
    }

    private void enviar(String destinatario, String assunto, String corpo) {
        try {
            SimpleMailMessage mensagem = new SimpleMailMessage();
            mensagem.setFrom(REMETENTE);
            mensagem.setTo(destinatario);
            mensagem.setSubject(assunto);
            mensagem.setText(corpo);
            mailSender.send(mensagem);
        } catch (Exception e) {
            // Notificacao e um efeito colateral, nunca deve derrubar o fluxo principal do agendamento.
            log.warn("Falha ao enviar notificacao por e-mail para {}: {}", destinatario, e.getMessage());
        }
    }
}
