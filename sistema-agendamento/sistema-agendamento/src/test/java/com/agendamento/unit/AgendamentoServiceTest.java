package com.agendamento.unit;

import com.agendamento.application.AgendamentoService;
import com.agendamento.domain.Agendamento;
import com.agendamento.domain.Profissional;
import com.agendamento.domain.StatusAgendamento;
import com.agendamento.infrastructure.AgendamentoRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AgendamentoServiceTest {

    @Test
    void naoDevePermitirAgendamentoDuplicado() {
        AgendamentoRepository repo = Mockito.mock(AgendamentoRepository.class);
        AgendamentoService service = new AgendamentoService(repo);

        Profissional p = new Profissional();
        p.setId(1L);

        Agendamento a = new Agendamento(
                null,
                LocalDateTime.now(),
                StatusAgendamento.CONFIRMADO,
                p,
                null,
                null
        );

        Mockito.when(repo.findByProfissionalIdAndDataHora(p.getId(), a.getDataHora()))
                .thenReturn(List.of(new Agendamento()));

        assertThrows(IllegalArgumentException.class, () -> service.criar(a));
    }

    @Test
    void deveCriarAgendamentoComSucesso() {
        AgendamentoRepository repo = Mockito.mock(AgendamentoRepository.class);
        AgendamentoService service = new AgendamentoService(repo);

        Profissional p = new Profissional();
        p.setId(1L);

        Agendamento a = new Agendamento(
                null,
                LocalDateTime.now(),
                null,
                p,
                null,
                null
        );

        Mockito.when(repo.findByProfissionalIdAndDataHora(p.getId(), a.getDataHora()))
                .thenReturn(List.of());
        Mockito.when(repo.save(Mockito.any())).thenAnswer(inv -> inv.getArgument(0));

        Agendamento criado = service.criar(a);

        assertEquals(StatusAgendamento.CONFIRMADO, criado.getStatus());
        Mockito.verify(repo, Mockito.times(1)).save(criado);
    }

    @Test
    void deveCancelarAgendamento() {
        AgendamentoRepository repo = Mockito.mock(AgendamentoRepository.class);
        AgendamentoService service = new AgendamentoService(repo);

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
        Mockito.verify(repo, Mockito.times(1)).save(cancelado);
    }
}
