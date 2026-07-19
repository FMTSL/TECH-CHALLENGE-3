package br.com.fiap.agendamento.application.usecase.calendario;

import br.com.fiap.agendamento.domain.model.Agendamento;
import br.com.fiap.agendamento.domain.repository.AgendamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

/**
 * Caso de uso: gera um feed iCalendar (RFC 5545) com os agendamentos de um cliente
 * ou de um profissional, permitindo sincronizacao com Google Calendar, Outlook e
 * Apple Calendar (feature 7 do desafio, que pede integracao "para os profissionais
 * e clientes").
 */
@Service
@RequiredArgsConstructor
public class GerarICalUseCase {

    private static final DateTimeFormatter ICAL_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");

    private final AgendamentoRepository agendamentoRepository;

    /** Feed do cliente: todos os agendamentos que ele fez. */
    public String executarPorCliente(UUID clienteId) {
        return gerar(agendamentoRepository.findByClienteId(clienteId));
    }

    /** Feed do profissional: todos os agendamentos da agenda dele. */
    public String executarPorProfissional(UUID profissionalId) {
        return gerar(agendamentoRepository.findByProfissionalId(profissionalId));
    }

    private String gerar(List<Agendamento> agendamentos) {
        StringBuilder ical = new StringBuilder();
        ical.append("BEGIN:VCALENDAR\r\n");
        ical.append("VERSION:2.0\r\n");
        ical.append("PRODID:-//Booking Beleza//Agendamentos//PT\r\n");

        for (Agendamento a : agendamentos) {
            ical.append("BEGIN:VEVENT\r\n");
            ical.append("UID:").append(a.getId()).append("@booking-beleza\r\n");
            ical.append("DTSTART:").append(formatar(a, Agendamento::getDataHora)).append("\r\n");
            ical.append("DTEND:").append(formatarFim(a)).append("\r\n");
            ical.append("SUMMARY:Agendamento - ").append(a.getStatus()).append("\r\n");
            ical.append("END:VEVENT\r\n");
        }

        ical.append("END:VCALENDAR\r\n");
        return ical.toString();
    }

    private String formatar(Agendamento a, Function<Agendamento, java.time.LocalDateTime> extrator) {
        return extrator.apply(a).atOffset(ZoneOffset.UTC).format(ICAL_FORMAT);
    }

    private String formatarFim(Agendamento a) {
        return a.getDataHora().plusHours(1).atOffset(ZoneOffset.UTC).format(ICAL_FORMAT);
    }
}
