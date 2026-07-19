package br.com.fiap.agendamento.application.usecase.disponibilidade;

import br.com.fiap.agendamento.application.dto.DisponibilidadeRequest;
import br.com.fiap.agendamento.domain.exception.RecursoNaoEncontradoException;
import br.com.fiap.agendamento.domain.exception.RegraDeNegocioException;
import br.com.fiap.agendamento.domain.model.DiaSemana;
import br.com.fiap.agendamento.domain.model.HorarioDisponivel;
import br.com.fiap.agendamento.domain.repository.HorarioDisponivelRepository;
import br.com.fiap.agendamento.domain.repository.ProfissionalRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("DefinirDisponibilidadeUseCase")
class DefinirDisponibilidadeUseCaseTest {

    @Mock private HorarioDisponivelRepository horarioDisponivelRepository;
    @Mock private ProfissionalRepository profissionalRepository;

    @InjectMocks
    private DefinirDisponibilidadeUseCase useCase;

    @Test
    @DisplayName("deve cadastrar janela de disponibilidade quando profissional existe e horarios sao validos")
    void deveCadastrarJanela() {
        UUID profissionalId = UUID.randomUUID();
        var request = new DisponibilidadeRequest(DiaSemana.TERCA, LocalTime.of(9, 0), LocalTime.of(18, 0));

        when(profissionalRepository.existsById(profissionalId)).thenReturn(true);
        when(horarioDisponivelRepository.save(any(HorarioDisponivel.class))).thenAnswer(inv -> inv.getArgument(0));

        HorarioDisponivel resultado = useCase.executar(profissionalId, request);

        assertThat(resultado.getDiaSemana()).isEqualTo(DiaSemana.TERCA);
        assertThat(resultado.getProfissionalId()).isEqualTo(profissionalId);
    }

    @Test
    @DisplayName("deve rejeitar quando profissional nao existe")
    void deveRejeitarProfissionalInexistente() {
        UUID profissionalId = UUID.randomUUID();
        var request = new DisponibilidadeRequest(DiaSemana.TERCA, LocalTime.of(9, 0), LocalTime.of(18, 0));

        when(profissionalRepository.existsById(profissionalId)).thenReturn(false);

        assertThatThrownBy(() -> useCase.executar(profissionalId, request))
                .isInstanceOf(RecursoNaoEncontradoException.class);

        verify(horarioDisponivelRepository, never()).save(any());
    }

    @Test
    @DisplayName("deve rejeitar quando horaInicio nao e anterior a horaFim")
    void deveRejeitarHorariosInvertidos() {
        UUID profissionalId = UUID.randomUUID();
        var request = new DisponibilidadeRequest(DiaSemana.TERCA, LocalTime.of(18, 0), LocalTime.of(9, 0));

        when(profissionalRepository.existsById(profissionalId)).thenReturn(true);

        assertThatThrownBy(() -> useCase.executar(profissionalId, request))
                .isInstanceOf(RegraDeNegocioException.class);

        verify(horarioDisponivelRepository, never()).save(any());
    }
}
