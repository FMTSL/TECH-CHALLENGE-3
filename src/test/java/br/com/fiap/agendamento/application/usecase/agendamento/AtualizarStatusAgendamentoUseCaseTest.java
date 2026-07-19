package br.com.fiap.agendamento.application.usecase.agendamento;

import br.com.fiap.agendamento.domain.exception.RecursoNaoEncontradoException;
import br.com.fiap.agendamento.domain.model.Agendamento;
import br.com.fiap.agendamento.domain.model.StatusAgendamento;
import br.com.fiap.agendamento.domain.repository.AgendamentoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AtualizarStatusAgendamentoUseCase")
class AtualizarStatusAgendamentoUseCaseTest {

    @Mock private AgendamentoRepository agendamentoRepository;

    @InjectMocks
    private AtualizarStatusAgendamentoUseCase useCase;

    @Test
    @DisplayName("deve atualizar o status do agendamento (painel do estabelecimento)")
    void deveAtualizarStatus() {
        UUID id = UUID.randomUUID();
        var agendamento = Agendamento.builder().id(id).status(StatusAgendamento.PENDENTE).build();

        when(agendamentoRepository.findById(id)).thenReturn(Optional.of(agendamento));
        when(agendamentoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Agendamento resultado = useCase.executar(id, StatusAgendamento.CONFIRMADO);

        assertThat(resultado.getStatus()).isEqualTo(StatusAgendamento.CONFIRMADO);
    }

    @Test
    @DisplayName("deve lancar excecao quando agendamento nao existe")
    void deveLancarExcecaoQuandoNaoEncontrado() {
        UUID id = UUID.randomUUID();
        when(agendamentoRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.executar(id, StatusAgendamento.NAO_COMPARECEU))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }
}
