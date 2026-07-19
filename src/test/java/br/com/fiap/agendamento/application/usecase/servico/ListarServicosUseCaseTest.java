package br.com.fiap.agendamento.application.usecase.servico;

import br.com.fiap.agendamento.domain.model.Servico;
import br.com.fiap.agendamento.domain.repository.ServicoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ListarServicosUseCase")
class ListarServicosUseCaseTest {

    @Mock private ServicoRepository servicoRepository;

    @InjectMocks
    private ListarServicosUseCase useCase;

    @Test
    @DisplayName("deve filtrar por estabelecimento quando id e informado")
    void deveFiltrarPorEstabelecimento() {
        UUID estabelecimentoId = UUID.randomUUID();
        var servico = Servico.builder().id(UUID.randomUUID()).estabelecimentoId(estabelecimentoId).build();
        when(servicoRepository.findByEstabelecimentoId(estabelecimentoId)).thenReturn(List.of(servico));

        List<Servico> resultado = useCase.executar(estabelecimentoId);

        assertThat(resultado).containsExactly(servico);
        verify(servicoRepository, never()).findAll();
    }

    @Test
    @DisplayName("deve listar todos quando nenhum estabelecimento e informado")
    void deveListarTodosSemFiltro() {
        var servico = Servico.builder().id(UUID.randomUUID()).build();
        when(servicoRepository.findAll()).thenReturn(List.of(servico));

        List<Servico> resultado = useCase.executar(null);

        assertThat(resultado).containsExactly(servico);
    }
}
