package br.com.fiap.agendamento.application.usecase.agendamento;

import br.com.fiap.agendamento.domain.model.Agendamento;
import br.com.fiap.agendamento.domain.repository.AgendamentoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ListarAgendamentosUseCase")
class ListarAgendamentosUseCaseTest {

    @Mock private AgendamentoRepository agendamentoRepository;

    @InjectMocks
    private ListarAgendamentosUseCase useCase;

    @Test
    @DisplayName("porCliente deve retornar os agendamentos do cliente ('meus agendamentos')")
    void devListarPorCliente() {
        UUID clienteId = UUID.randomUUID();
        var agendamento = Agendamento.builder().id(UUID.randomUUID()).clienteId(clienteId).build();
        when(agendamentoRepository.findByClienteId(clienteId)).thenReturn(List.of(agendamento));

        assertThat(useCase.porCliente(clienteId)).containsExactly(agendamento);
    }

    @Test
    @DisplayName("porEstabelecimento deve retornar os agendamentos do painel de gestao")
    void devListarPorEstabelecimento() {
        UUID estabelecimentoId = UUID.randomUUID();
        var agendamento = Agendamento.builder().id(UUID.randomUUID()).estabelecimentoId(estabelecimentoId).build();
        when(agendamentoRepository.findByEstabelecimentoId(estabelecimentoId)).thenReturn(List.of(agendamento));

        assertThat(useCase.porEstabelecimento(estabelecimentoId)).containsExactly(agendamento);
    }
}
