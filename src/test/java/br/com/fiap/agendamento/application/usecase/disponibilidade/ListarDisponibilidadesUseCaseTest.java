package br.com.fiap.agendamento.application.usecase.disponibilidade;

import br.com.fiap.agendamento.domain.model.DiaSemana;
import br.com.fiap.agendamento.domain.model.HorarioDisponivel;
import br.com.fiap.agendamento.domain.repository.HorarioDisponivelRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ListarDisponibilidadesUseCase")
class ListarDisponibilidadesUseCaseTest {

    @Mock private HorarioDisponivelRepository horarioDisponivelRepository;

    @InjectMocks
    private ListarDisponibilidadesUseCase useCase;

    @Test
    @DisplayName("deve listar as janelas de disponibilidade cadastradas para o profissional")
    void deveListarJanelas() {
        UUID profissionalId = UUID.randomUUID();
        var janela = HorarioDisponivel.builder()
                .profissionalId(profissionalId)
                .diaSemana(DiaSemana.SEGUNDA)
                .horaInicio(LocalTime.of(9, 0))
                .horaFim(LocalTime.of(18, 0))
                .build();

        when(horarioDisponivelRepository.findByProfissionalId(profissionalId)).thenReturn(List.of(janela));

        List<HorarioDisponivel> resultado = useCase.executar(profissionalId);

        assertThat(resultado).containsExactly(janela);
    }
}
