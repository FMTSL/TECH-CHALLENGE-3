package br.com.fiap.agendamento.infrastructure.notification;

import br.com.fiap.agendamento.application.port.NotificacaoService;
import br.com.fiap.agendamento.domain.model.Agendamento;
import br.com.fiap.agendamento.domain.model.StatusAgendamento;
import br.com.fiap.agendamento.domain.repository.AgendamentoRepository;
import br.com.fiap.agendamento.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Job agendado (feature 3: "lembretes automaticos"). Roda de hora em hora e
 * dispara o lembrete para agendamentos confirmados que acontecem nas proximas
 * ~24h e ainda nao receberam lembrete, marcando a flag para nao duplicar o envio.
 */
@Component
@RequiredArgsConstructor
public class LembreteAgendamentoScheduler {

    private final AgendamentoRepository agendamentoRepository;
    private final UsuarioRepository usuarioRepository;
    private final NotificacaoService notificacaoService;

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void dispararLembretes() {
        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime inicioJanela = agora.plusHours(23);
        LocalDateTime fimJanela = agora.plusHours(25);

        var agendamentos = agendamentoRepository.findByStatusAndLembreteEnviadoFalseAndDataHoraBetween(
                StatusAgendamento.CONFIRMADO, inicioJanela, fimJanela);

        for (Agendamento agendamento : agendamentos) {
            usuarioRepository.findById(agendamento.getClienteId()).ifPresent(cliente -> {
                notificacaoService.enviarLembrete(agendamento, cliente.getEmail());
                agendamento.setLembreteEnviado(true);
                agendamentoRepository.save(agendamento);
            });
        }
    }
}
