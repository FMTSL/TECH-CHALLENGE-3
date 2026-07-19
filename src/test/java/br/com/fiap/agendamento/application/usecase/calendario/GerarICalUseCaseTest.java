package br.com.fiap.agendamento.application.usecase.calendario;

import br.com.fiap.agendamento.domain.model.Agendamento;
import br.com.fiap.agendamento.domain.model.StatusAgendamento;
import br.com.fiap.agendamento.domain.repository.AgendamentoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GerarICalUseCase")
class GerarICalUseCaseTest {

    @Mock private AgendamentoRepository agendamentoRepository;

    @InjectMocks
    private GerarICalUseCase useCase;

    @Test
    @DisplayName("deve gerar feed iCalendar valido com os agendamentos do cliente")
    void deveGerarFeedDoCliente() {
        UUID clienteId = UUID.randomUUID();
        var agendamento = Agendamento.builder().id(UUID.randomUUID())
                .dataHora(LocalDateTime.now().plusDays(1)).status(StatusAgendamento.CONFIRMADO).build();

        when(agendamentoRepository.findByClienteId(clienteId)).thenReturn(List.of(agendamento));

        String ical = useCase.executarPorCliente(clienteId);

        assertThat(ical).startsWith("BEGIN:VCALENDAR").endsWith("END:VCALENDAR\r\n");
        assertThat(ical).contains("BEGIN:VEVENT").contains("UID:" + agendamento.getId());
    }

    @Test
    @DisplayName("deve gerar feed iCalendar valido com os agendamentos do profissional")
    void deveGerarFeedDoProfissional() {
        UUID profissionalId = UUID.randomUUID();
        when(agendamentoRepository.findByProfissionalId(profissionalId)).thenReturn(List.of());

        String ical = useCase.executarPorProfissional(profissionalId);

        assertThat(ical).isEqualTo("BEGIN:VCALENDAR\r\nVERSION:2.0\r\nPRODID:-//Booking Beleza//Agendamentos//PT\r\nEND:VCALENDAR\r\n");
    }
}
