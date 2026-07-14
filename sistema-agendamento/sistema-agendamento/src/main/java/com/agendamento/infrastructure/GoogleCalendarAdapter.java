package com.agendamento.infrastructure;

import com.agendamento.domain.Agendamento;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
public class GoogleCalendarAdapter {
    private final Calendar calendar;

    public GoogleCalendarAdapter(Calendar calendar) {
        this.calendar = calendar;
    }

    public void sincronizarAgendamento(Agendamento agendamento) throws IOException {
        Event event = new Event()
                .setSummary("Agendamento com " + agendamento.getProfissional().getNome())
                .setLocation(agendamento.getEstabelecimento().getEndereco())
                .setDescription("Status: " + agendamento.getStatus());

        ZonedDateTime inicio = agendamento.getDataHora().atZone(ZoneId.systemDefault());
        ZonedDateTime fim = agendamento.getDataHora().plusHours(1).atZone(ZoneId.systemDefault());

        DateTime startDateTime = new DateTime(inicio.toInstant().toEpochMilli());
        DateTime endDateTime = new DateTime(fim.toInstant().toEpochMilli());

        EventDateTime start = new EventDateTime()
                .setDateTime(startDateTime)
                .setTimeZone(ZoneId.systemDefault().toString());

        EventDateTime end = new EventDateTime()
                .setDateTime(endDateTime)
                .setTimeZone(ZoneId.systemDefault().toString());

        event.setStart(start);
        event.setEnd(end);

        calendar.events().insert("primary", event).execute();
    }
}
