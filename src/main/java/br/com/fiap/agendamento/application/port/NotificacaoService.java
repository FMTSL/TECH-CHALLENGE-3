package br.com.fiap.agendamento.application.port;

import br.com.fiap.agendamento.domain.model.Agendamento;

/**
 * Port de saida para envio de confirmacoes e lembretes (feature 3 do desafio).
 * A implementacao concreta (e-mail via SMTP/MailHog) fica em infrastructure,
 * mantendo os use cases livres de detalhes de transporte de notificacao.
 */
public interface NotificacaoService {
    void enviarConfirmacao(Agendamento agendamento, String emailCliente);
    void enviarCancelamento(Agendamento agendamento, String emailCliente);
    void enviarLembrete(Agendamento agendamento, String emailCliente);
}
