package br.com.fiap.agendamento.application.usecase.estabelecimento;

import br.com.fiap.agendamento.application.dto.EstabelecimentoRequest;
import br.com.fiap.agendamento.domain.model.Estabelecimento;
import br.com.fiap.agendamento.domain.repository.EstabelecimentoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CadastrarEstabelecimentoUseCase")
class CadastrarEstabelecimentoUseCaseTest {

    @Mock private EstabelecimentoRepository estabelecimentoRepository;

    @InjectMocks
    private CadastrarEstabelecimentoUseCase useCase;

    @Test
    @DisplayName("deve cadastrar estabelecimento vinculado ao usuario dono autenticado")
    void deveCadastrarEstabelecimento() {
        UUID donoId = UUID.randomUUID();
        var request = new EstabelecimentoRequest("Salao Bela", "Rua A, 123", "Sao Paulo", "09h-19h", List.of("foto1.jpg"));

        when(estabelecimentoRepository.save(any(Estabelecimento.class))).thenAnswer(inv -> inv.getArgument(0));

        Estabelecimento resultado = useCase.executar(request, donoId);

        assertThat(resultado.getNome()).isEqualTo("Salao Bela");
        assertThat(resultado.getCidade()).isEqualTo("Sao Paulo");
        assertThat(resultado.getUsuarioDonoId()).isEqualTo(donoId);
        assertThat(resultado.getFotos()).containsExactly("foto1.jpg");
        verify(estabelecimentoRepository).save(any(Estabelecimento.class));
    }
}
