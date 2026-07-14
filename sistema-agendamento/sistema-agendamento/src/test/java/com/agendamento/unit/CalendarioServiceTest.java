package com.agendamento.unit;

import com.agendamento.application.CalendarioService;
import com.agendamento.domain.Agendamento;
import com.agendamento.domain.StatusAgendamento;
import com.agendamento.infrastructure.GoogleCalendarAdapter;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class CalendarioServiceTest {

    @Test
    void deveSincronizarComGoogle() throws IOException {
        GoogleCalendarAdapter adapter = Mockito.mock(GoogleCalendarAdapter.class);
        CalendarioService service = new CalendarioService(adapter);

        Agendamento a = new Agendamento(
                1L,
                LocalDateTime.now(),
                StatusAgendamento.CONFIRMADO,
                null,
                null,
                null
        );

        assertDoesNotThrow(() -> service.sincronizarComGoogle(a));

        Mockito.verify(adapter, Mockito.times(1)).sincronizarAgendamento(a);
    }

}
