package br.com.fiap.agendamento.infrastructure.web.controller;

import br.com.fiap.agendamento.application.usecase.calendario.GerarICalUseCase;
import br.com.fiap.agendamento.infrastructure.web.AutenticacaoUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Integracao com calendarios externos (feature 7 do desafio): expoe feeds iCalendar
 * (RFC 5545) tanto para o cliente autenticado quanto para o profissional, prontos
 * para assinatura no Google Calendar, Outlook ou Apple Calendar.
 */
@RestController
@RequestMapping("/api/calendario")
@RequiredArgsConstructor
@Tag(name = "Calendario", description = "Feed iCalendar para integracao com Google Calendar/Outlook/Apple Calendar")
public class CalendarioController {

    private final GerarICalUseCase gerarICalUseCase;

    /** Feed do cliente autenticado com todos os seus agendamentos. */
    @GetMapping(value = "/meu-feed.ics", produces = "text/calendar")
    public ResponseEntity<String> meuFeed() {
        var clienteId = AutenticacaoUtils.usuarioAutenticadoId();
        return respostaIcs(gerarICalUseCase.executarPorCliente(clienteId));
    }

    /**
     * Feed do profissional (sem autenticacao propria, ja que ele nao possui login no
     * sistema): a URL com o UUID do profissional funciona como link de assinatura
     * privado, no mesmo modelo usado por Google Calendar/Outlook para feeds externos.
     */
    @GetMapping(value = "/profissionais/{profissionalId}/feed.ics", produces = "text/calendar")
    public ResponseEntity<String> feedDoProfissional(@PathVariable UUID profissionalId) {
        return respostaIcs(gerarICalUseCase.executarPorProfissional(profissionalId));
    }

    private ResponseEntity<String> respostaIcs(String ical) {
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/calendar"))
                .header("Content-Disposition", "attachment; filename=agenda.ics")
                .body(ical);
    }
}
