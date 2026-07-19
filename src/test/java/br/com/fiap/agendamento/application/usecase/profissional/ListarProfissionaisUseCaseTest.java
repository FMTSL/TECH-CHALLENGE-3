package br.com.fiap.agendamento.application.usecase.profissional;

import br.com.fiap.agendamento.domain.model.Profissional;
import br.com.fiap.agendamento.domain.repository.ProfissionalRepository;
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
@DisplayName("ListarProfissionaisUseCase")
class ListarProfissionaisUseCaseTest {

    @Mock private ProfissionalRepository profissionalRepository;

    @InjectMocks
    private ListarProfissionaisUseCase useCase;

    @Test
    @DisplayName("deve filtrar por estabelecimento quando id e informado")
    void deveFiltrarPorEstabelecimento() {
        UUID estabelecimentoId = UUID.randomUUID();
        var profissional = Profissional.builder().id(UUID.randomUUID()).estabelecimentoId(estabelecimentoId).build();
        when(profissionalRepository.findByEstabelecimentoId(estabelecimentoId)).thenReturn(List.of(profissional));

        List<Profissional> resultado = useCase.executar(estabelecimentoId);

        assertThat(resultado).containsExactly(profissional);
        verify(profissionalRepository, never()).findAll();
    }

    @Test
    @DisplayName("deve listar todos quando nenhum estabelecimento e informado")
    void deveListarTodosSemFiltro() {
        var profissional = Profissional.builder().id(UUID.randomUUID()).build();
        when(profissionalRepository.findAll()).thenReturn(List.of(profissional));

        List<Profissional> resultado = useCase.executar(null);

        assertThat(resultado).containsExactly(profissional);
    }
}
