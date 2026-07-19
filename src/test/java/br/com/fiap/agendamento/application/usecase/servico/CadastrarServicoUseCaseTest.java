package br.com.fiap.agendamento.application.usecase.servico;

import br.com.fiap.agendamento.application.dto.ServicoRequest;
import br.com.fiap.agendamento.domain.exception.RecursoNaoEncontradoException;
import br.com.fiap.agendamento.domain.model.Servico;
import br.com.fiap.agendamento.domain.repository.EstabelecimentoRepository;
import br.com.fiap.agendamento.domain.repository.ServicoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CadastrarServicoUseCase")
class CadastrarServicoUseCaseTest {

    @Mock private ServicoRepository servicoRepository;
    @Mock private EstabelecimentoRepository estabelecimentoRepository;

    @InjectMocks
    private CadastrarServicoUseCase useCase;

    @Test
    @DisplayName("deve cadastrar servico quando estabelecimento existe")
    void deveCadastrarServico() {
        UUID estabelecimentoId = UUID.randomUUID();
        var request = new ServicoRequest("Corte", "Corte simples", BigDecimal.valueOf(50), 30, estabelecimentoId);

        when(estabelecimentoRepository.existsById(estabelecimentoId)).thenReturn(true);
        when(servicoRepository.save(any(Servico.class))).thenAnswer(inv -> inv.getArgument(0));

        Servico resultado = useCase.executar(request);

        assertThat(resultado.getNome()).isEqualTo("Corte");
        assertThat(resultado.getPreco()).isEqualByComparingTo(BigDecimal.valueOf(50));
        assertThat(resultado.getDuracaoMinutos()).isEqualTo(30);
    }

    @Test
    @DisplayName("deve rejeitar quando estabelecimento nao existe")
    void deveRejeitarEstabelecimentoInexistente() {
        UUID estabelecimentoId = UUID.randomUUID();
        var request = new ServicoRequest("Corte", null, BigDecimal.TEN, 30, estabelecimentoId);

        when(estabelecimentoRepository.existsById(estabelecimentoId)).thenReturn(false);

        assertThatThrownBy(() -> useCase.executar(request))
                .isInstanceOf(RecursoNaoEncontradoException.class);

        verify(servicoRepository, never()).save(any());
    }
}
