package br.com.fiap.agendamento.application.usecase.avaliacao;

import br.com.fiap.agendamento.application.dto.CriarAvaliacaoRequest;
import br.com.fiap.agendamento.domain.exception.RecursoNaoEncontradoException;
import br.com.fiap.agendamento.domain.exception.RegraDeNegocioException;
import br.com.fiap.agendamento.domain.model.Agendamento;
import br.com.fiap.agendamento.domain.model.Avaliacao;
import br.com.fiap.agendamento.domain.model.StatusAgendamento;
import br.com.fiap.agendamento.domain.repository.AgendamentoRepository;
import br.com.fiap.agendamento.domain.repository.AvaliacaoRepository;
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
@DisplayName("CriarAvaliacaoUseCase")
class CriarAvaliacaoUseCaseTest {

    @Mock private AvaliacaoRepository avaliacaoRepository;
    @Mock private AgendamentoRepository agendamentoRepository;

    @InjectMocks
    private CriarAvaliacaoUseCase useCase;

    @Test
    @DisplayName("deve criar avaliacao quando agendamento esta concluido e ainda nao foi avaliado")
    void deveCriarAvaliacao() {
        UUID agendamentoId = UUID.randomUUID();
        UUID clienteId = UUID.randomUUID();
        var agendamento = Agendamento.builder().id(agendamentoId).clienteId(clienteId)
                .estabelecimentoId(UUID.randomUUID()).profissionalId(UUID.randomUUID())
                .status(StatusAgendamento.CONCLUIDO).build();
        var request = new CriarAvaliacaoRequest(agendamentoId, 5, "Otimo atendimento");

        when(agendamentoRepository.findByIdAndClienteId(agendamentoId, clienteId)).thenReturn(Optional.of(agendamento));
        when(avaliacaoRepository.existsByAgendamentoId(agendamentoId)).thenReturn(false);
        when(avaliacaoRepository.save(any(Avaliacao.class))).thenAnswer(inv -> inv.getArgument(0));

        Avaliacao resultado = useCase.executar(request, clienteId);

        assertThat(resultado.getNota()).isEqualTo(5);
        assertThat(resultado.getAgendamentoId()).isEqualTo(agendamentoId);
    }

    @Test
    @DisplayName("deve rejeitar avaliacao de agendamento nao concluido")
    void deveRejeitarAgendamentoNaoConcluido() {
        UUID agendamentoId = UUID.randomUUID();
        UUID clienteId = UUID.randomUUID();
        var agendamento = Agendamento.builder().id(agendamentoId).clienteId(clienteId)
                .status(StatusAgendamento.PENDENTE).build();
        var request = new CriarAvaliacaoRequest(agendamentoId, 5, null);

        when(agendamentoRepository.findByIdAndClienteId(agendamentoId, clienteId)).thenReturn(Optional.of(agendamento));

        assertThatThrownBy(() -> useCase.executar(request, clienteId))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("concluidos");
    }

    @Test
    @DisplayName("deve rejeitar avaliacao duplicada do mesmo agendamento")
    void deveRejeitarAvaliacaoDuplicada() {
        UUID agendamentoId = UUID.randomUUID();
        UUID clienteId = UUID.randomUUID();
        var agendamento = Agendamento.builder().id(agendamentoId).clienteId(clienteId)
                .status(StatusAgendamento.CONCLUIDO).build();
        var request = new CriarAvaliacaoRequest(agendamentoId, 5, null);

        when(agendamentoRepository.findByIdAndClienteId(agendamentoId, clienteId)).thenReturn(Optional.of(agendamento));
        when(avaliacaoRepository.existsByAgendamentoId(agendamentoId)).thenReturn(true);

        assertThatThrownBy(() -> useCase.executar(request, clienteId))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("ja foi avaliado");
    }

    @Test
    @DisplayName("deve lancar excecao quando agendamento nao encontrado")
    void deveLancarExcecaoAgendamentoNaoEncontrado() {
        UUID agendamentoId = UUID.randomUUID();
        UUID clienteId = UUID.randomUUID();
        var request = new CriarAvaliacaoRequest(agendamentoId, 5, null);

        when(agendamentoRepository.findByIdAndClienteId(agendamentoId, clienteId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.executar(request, clienteId))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }
}
