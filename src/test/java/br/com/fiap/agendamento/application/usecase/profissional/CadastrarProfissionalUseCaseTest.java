package br.com.fiap.agendamento.application.usecase.profissional;

import br.com.fiap.agendamento.application.dto.ProfissionalRequest;
import br.com.fiap.agendamento.domain.exception.RecursoNaoEncontradoException;
import br.com.fiap.agendamento.domain.model.Profissional;
import br.com.fiap.agendamento.domain.repository.EstabelecimentoRepository;
import br.com.fiap.agendamento.domain.repository.ProfissionalRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CadastrarProfissionalUseCase")
class CadastrarProfissionalUseCaseTest {

    @Mock private ProfissionalRepository profissionalRepository;
    @Mock private EstabelecimentoRepository estabelecimentoRepository;

    @InjectMocks
    private CadastrarProfissionalUseCase useCase;

    @Test
    @DisplayName("deve cadastrar profissional quando estabelecimento existe")
    void deveCadastrarProfissional() {
        UUID estabelecimentoId = UUID.randomUUID();
        var request = new ProfissionalRequest("Ana", List.of("Cabelo"), BigDecimal.valueOf(80), estabelecimentoId, "ana@salao.com");

        when(estabelecimentoRepository.existsById(estabelecimentoId)).thenReturn(true);
        when(profissionalRepository.save(any(Profissional.class))).thenAnswer(inv -> inv.getArgument(0));

        Profissional resultado = useCase.executar(request);

        assertThat(resultado.getNome()).isEqualTo("Ana");
        assertThat(resultado.getEmailContato()).isEqualTo("ana@salao.com");
        assertThat(resultado.getEstabelecimentoId()).isEqualTo(estabelecimentoId);
    }

    @Test
    @DisplayName("deve rejeitar quando estabelecimento nao existe")
    void deveRejeitarEstabelecimentoInexistente() {
        UUID estabelecimentoId = UUID.randomUUID();
        var request = new ProfissionalRequest("Ana", List.of(), null, estabelecimentoId, null);

        when(estabelecimentoRepository.existsById(estabelecimentoId)).thenReturn(false);

        assertThatThrownBy(() -> useCase.executar(request))
                .isInstanceOf(RecursoNaoEncontradoException.class);

        verify(profissionalRepository, never()).save(any());
    }
}
