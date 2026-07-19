package br.com.fiap.agendamento.application.usecase.estabelecimento;

import br.com.fiap.agendamento.application.dto.FiltroBuscaEstabelecimento;
import br.com.fiap.agendamento.application.usecase.disponibilidade.ConsultarDisponibilidadeUseCase;
import br.com.fiap.agendamento.domain.exception.RecursoNaoEncontradoException;
import br.com.fiap.agendamento.domain.model.Avaliacao;
import br.com.fiap.agendamento.domain.model.Estabelecimento;
import br.com.fiap.agendamento.domain.repository.AvaliacaoRepository;
import br.com.fiap.agendamento.domain.repository.EstabelecimentoRepository;
import br.com.fiap.agendamento.domain.repository.ProfissionalRepository;
import br.com.fiap.agendamento.domain.repository.ServicoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("BuscarEstabelecimentosUseCase")
class BuscarEstabelecimentosUseCaseTest {

    @Mock private EstabelecimentoRepository estabelecimentoRepository;
    @Mock private ServicoRepository servicoRepository;
    @Mock private AvaliacaoRepository avaliacaoRepository;
    @Mock private ProfissionalRepository profissionalRepository;
    @Mock private ConsultarDisponibilidadeUseCase consultarDisponibilidadeUseCase;

    @InjectMocks
    private BuscarEstabelecimentosUseCase useCase;

    @Test
    @DisplayName("deve retornar todos quando nenhum filtro e informado")
    void deveRetornarTodosSemFiltro() {
        var estabelecimento = Estabelecimento.builder().id(UUID.randomUUID()).nome("Salao Bela").build();
        when(estabelecimentoRepository.findAll()).thenReturn(List.of(estabelecimento));

        var filtro = new FiltroBuscaEstabelecimento(null, null, null, null, null, null);
        List<Estabelecimento> resultado = useCase.executar(filtro);

        assertThat(resultado).containsExactly(estabelecimento);
    }

    @Test
    @DisplayName("deve filtrar por nota minima calculando a media das avaliacoes")
    void deveFiltrarPorNotaMinima() {
        var estabelecimentoBom = Estabelecimento.builder().id(UUID.randomUUID()).nome("Bom").build();
        var estabelecimentoRuim = Estabelecimento.builder().id(UUID.randomUUID()).nome("Ruim").build();
        when(estabelecimentoRepository.findAll()).thenReturn(List.of(estabelecimentoBom, estabelecimentoRuim));

        when(avaliacaoRepository.findByEstabelecimentoId(estabelecimentoBom.getId()))
                .thenReturn(List.of(Avaliacao.builder().nota(5).build()));
        when(avaliacaoRepository.findByEstabelecimentoId(estabelecimentoRuim.getId()))
                .thenReturn(List.of(Avaliacao.builder().nota(2).build()));

        var filtro = new FiltroBuscaEstabelecimento(null, null, null, null, 4.0, null);
        List<Estabelecimento> resultado = useCase.executar(filtro);

        assertThat(resultado).containsExactly(estabelecimentoBom);
    }

    @Test
    @DisplayName("buscarPorId deve lancar excecao quando nao encontrado")
    void deveLancarExcecaoQuandoNaoEncontrado() {
        UUID id = UUID.randomUUID();
        when(estabelecimentoRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.buscarPorId(id))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }
}
