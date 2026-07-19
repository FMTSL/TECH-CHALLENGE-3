package br.com.fiap.agendamento.application.usecase.disponibilidade;

import br.com.fiap.agendamento.domain.model.Agendamento;
import br.com.fiap.agendamento.domain.model.DiaSemana;
import br.com.fiap.agendamento.domain.model.HorarioDisponivel;
import br.com.fiap.agendamento.domain.model.StatusAgendamento;
import br.com.fiap.agendamento.domain.repository.AgendamentoRepository;
import br.com.fiap.agendamento.domain.repository.HorarioDisponivelRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ConsultarDisponibilidadeUseCase")
class ConsultarDisponibilidadeUseCaseTest {

    @Mock private HorarioDisponivelRepository horarioDisponivelRepository;
    @Mock private AgendamentoRepository agendamentoRepository;

    @InjectMocks
    private ConsultarDisponibilidadeUseCase useCase;

    @Test
    @DisplayName("deve gerar slots de 30 em 30 minutos excluindo horarios ja ocupados")
    void deveGerarSlotsLivresExcluindoOcupados() {
        UUID profissionalId = UUID.randomUUID();
        LocalDate proximaTerca = proximaDataParaDiaDaSemana(java.time.DayOfWeek.TUESDAY);

        var janela = HorarioDisponivel.builder()
                .profissionalId(profissionalId)
                .diaSemana(DiaSemana.TERCA)
                .horaInicio(LocalTime.of(9, 0))
                .horaFim(LocalTime.of(10, 0))
                .build();

        var ocupado = Agendamento.builder()
                .profissionalId(profissionalId)
                .dataHora(proximaTerca.atTime(9, 30))
                .status(StatusAgendamento.CONFIRMADO)
                .build();

        when(horarioDisponivelRepository.findByProfissionalIdAndDiaSemana(profissionalId, DiaSemana.TERCA))
                .thenReturn(List.of(janela));
        when(agendamentoRepository.findByProfissionalIdAndDataHoraBetween(any(), any(), any()))
                .thenReturn(List.of(ocupado));

        List<LocalDateTime> livres = useCase.executar(profissionalId, proximaTerca);

        assertThat(livres)
                .contains(proximaTerca.atTime(9, 0))
                .doesNotContain(proximaTerca.atTime(9, 30));
    }

    @Test
    @DisplayName("deve retornar lista vazia quando profissional nao tem janela cadastrada no dia")
    void deveRetornarVazioSemJanela() {
        UUID profissionalId = UUID.randomUUID();
        LocalDate data = LocalDate.now().plusDays(10);

        when(horarioDisponivelRepository.findByProfissionalIdAndDiaSemana(any(), any())).thenReturn(List.of());

        assertThat(useCase.executar(profissionalId, data)).isEmpty();
    }

    @Test
    @DisplayName("estaDentroDeAlgumaJanela deve permitir quando profissional nao configurou agenda (compatibilidade)")
    void devePermitirQuandoSemAgendaConfigurada() {
        UUID profissionalId = UUID.randomUUID();
        when(horarioDisponivelRepository.findByProfissionalIdAndDiaSemana(any(), any())).thenReturn(List.of());

        boolean resultado = useCase.estaDentroDeAlgumaJanela(profissionalId, LocalDateTime.now().plusDays(1));

        assertThat(resultado).isTrue();
    }

    private LocalDate proximaDataParaDiaDaSemana(java.time.DayOfWeek diaDesejado) {
        LocalDate data = LocalDate.now().plusDays(1);
        while (data.getDayOfWeek() != diaDesejado) {
            data = data.plusDays(1);
        }
        return data;
    }
}
