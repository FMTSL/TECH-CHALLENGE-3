package com.agendamento.application;

import com.agendamento.application.exception.HorarioOcupadoException;
import com.agendamento.application.exception.AgendamentoNaoEncontradoException;
import com.agendamento.domain.Agendamento;
import com.agendamento.domain.Profissional;
import com.agendamento.domain.StatusAgendamento;
import com.agendamento.infrastructure.AgendamentoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class AgendamentoServiceTest {

    private AgendamentoService service;

    @Mock
    private AgendamentoRepository repositoryMock;

    private AutoCloseable openMocks;

    @BeforeEach
    void setup() {

        openMocks = MockitoAnnotations.openMocks(this);

        service = new AgendamentoService(repositoryMock);
    }

    @AfterEach
    void tearDown() throws Exception {

        openMocks.close();
    }

    @Test
    void deveCriarAgendamentoComSucesso() {

        LocalDateTime dataHora = LocalDateTime.now().plusDays(1);
        Profissional prof = new Profissional(1L, "João", "Cabelereiro", "08-18h", 100.0, null);
        Agendamento agendamento = new Agendamento(null, dataHora, null, prof, null, null);

        when(repositoryMock.findByProfissionalIdAndDataHora(1L, dataHora))
                .thenReturn(Collections.emptyList());

        when(repositoryMock.save(any(Agendamento.class))).thenAnswer(invocation -> {
            Agendamento salvo = invocation.getArgument(0);
            salvo.setId(10L);
            return salvo;
        });

        Agendamento resultado = service.criar(agendamento);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(10L);
        assertThat(resultado.getStatus()).isEqualTo(StatusAgendamento.CONFIRMADO);

        verify(repositoryMock, times(1)).findByProfissionalIdAndDataHora(1L, dataHora);
        verify(repositoryMock, times(1)).save(any(Agendamento.class));
    }

    @Test
    void deveLancarExcecao_QuandoHorarioEstiverOcupado() {

        LocalDateTime dataHora = LocalDateTime.now().plusDays(1);
        Profissional prof = new Profissional(1L, "João", "Cabelereiro", "08-18h", 100.0, null);
        Agendamento agendamento = new Agendamento(null, dataHora, null, prof, null, null);

        Agendamento agendamentoExistente = new Agendamento(2L, dataHora, StatusAgendamento.CONFIRMADO, prof, null, null);

        when(repositoryMock.findByProfissionalIdAndDataHora(1L, dataHora))
                .thenReturn(List.of(agendamentoExistente));


        assertThatThrownBy(() -> service.criar(agendamento))
                .isInstanceOf(HorarioOcupadoException.class)
                .hasMessage("Horário já ocupado para este profissional.");


        verify(repositoryMock, never()).save(any(Agendamento.class));
    }

    @Test
    void deveLancarExcecao_QuandoCancelarAgendamentoInexistente() {

        Long idInexistente = 99L;
        when(repositoryMock.findById(idInexistente)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.cancelar(idInexistente))
                .isInstanceOf(AgendamentoNaoEncontradoException.class)
                .hasMessage("Agendamento não encontrado");

        verify(repositoryMock, never()).save(any(Agendamento.class));
    }
}