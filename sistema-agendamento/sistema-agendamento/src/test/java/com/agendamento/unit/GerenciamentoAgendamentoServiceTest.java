package com.agendamento.unit;

import com.agendamento.application.GerenciamentoAgendamentoService;
import com.agendamento.domain.Agendamento;
import com.agendamento.domain.StatusAgendamento;
import com.agendamento.infrastructure.AgendamentoRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class GerenciamentoAgendamentoServiceTest {

    @Test
    void deveCancelarAgendamento() {
        AgendamentoRepository repo = Mockito.mock(AgendamentoRepository.class);
        GerenciamentoAgendamentoService service = new GerenciamentoAgendamentoService(repo);

        Agendamento a = new Agendamento(
                1L,
                LocalDateTime.now(),
                StatusAgendamento.CONFIRMADO,
                null,
                null,
                null
        );

        Mockito.when(repo.findById(1L)).thenReturn(Optional.of(a));
        Mockito.when(repo.save(Mockito.any())).thenAnswer(inv -> inv.getArgument(0));

        Agendamento cancelado = service.cancelar(1L);

        assertEquals(StatusAgendamento.CANCELADO, cancelado.getStatus());
    }
}
