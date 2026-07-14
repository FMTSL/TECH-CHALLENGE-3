package com.agendamento.application;

import com.agendamento.domain.Agendamento;
import com.agendamento.infrastructure.GoogleCalendarAdapter;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class CalendarioService {
    private final GoogleCalendarAdapter googleAdapter;

    public CalendarioService(GoogleCalendarAdapter googleAdapter) {
        this.googleAdapter = googleAdapter;
    }

    public void sincronizarComGoogle(Agendamento agendamento) {
        try {
            googleAdapter.sincronizarAgendamento(agendamento);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao sincronizar com Google Calendar", e);
        }
    }
}
