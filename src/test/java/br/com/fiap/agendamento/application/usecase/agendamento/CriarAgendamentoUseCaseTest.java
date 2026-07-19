package br.com.fiap.agendamento.application.usecase.agendamento;

import br.com.fiap.agendamento.application.dto.CriarAgendamentoRequest;
import br.com.fiap.agendamento.application.port.NotificacaoService;
import br.com.fiap.agendamento.application.usecase.disponibilidade.ConsultarDisponibilidadeUseCase;
import br.com.fiap.agendamento.domain.exception.RecursoNaoEncontradoException;
import br.com.fiap.agendamento.domain.exception.RegraDeNegocioException;
import br.com.fiap.agendamento.domain.model.Agendamento;
import br.com.fiap.agendamento.domain.model.Profissional;
import br.com.fiap.agendamento.domain.model.Servico;
import br.com.fiap.agendamento.domain.model.Usuario;
import br.com.fiap.agendamento.domain.repository.AgendamentoRepository;
import br.com.fiap.agendamento.domain.repository.ProfissionalRepository;
import br.com.fiap.agendamento.domain.repository.ServicoRepository;
import br.com.fiap.agendamento.domain.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Testes TDD do caso de uso central do sistema: criacao de agendamento.
 * Escritos ANTES da implementacao final, guiando o design da regra de double-booking
 * e da validacao de disponibilidade do profissional.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CriarAgendamentoUseCase")
class CriarAgendamentoUseCaseTest {

    @Mock private AgendamentoRepository agendamentoRepository;
    @Mock private ProfissionalRepository profissionalRepository;
    @Mock private ServicoRepository servicoRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private ConsultarDisponibilidadeUseCase consultarDisponibilidadeUseCase;
    @Mock private NotificacaoService notificacaoService;

    @InjectMocks
    private CriarAgendamentoUseCase useCase;

    private UUID profissionalId;
    private UUID servicoId;
    private UUID clienteId;
    private UUID estabelecimentoId;
    private LocalDateTime dataHora;
    private CriarAgendamentoRequest request;
    private Usuario cliente;

    @BeforeEach
    void setUp() {
        profissionalId = UUID.randomUUID();
        servicoId = UUID.randomUUID();
        clienteId = UUID.randomUUID();
        estabelecimentoId = UUID.randomUUID();
        dataHora = LocalDateTime.now().plusDays(1);
        request = new CriarAgendamentoRequest(profissionalId, servicoId, dataHora);
        cliente = Usuario.builder().id(clienteId).email("cliente@email.com").build();
    }

    @Test
    @DisplayName("deve criar agendamento e notificar o cliente quando tudo e valido")
    void deveCriarAgendamentoComSucesso() {
        var profissional = Profissional.builder().id(profissionalId).nome("Ana").build();
        var servico = Servico.builder().id(servicoId).estabelecimentoId(estabelecimentoId)
                .nome("Corte").preco(BigDecimal.TEN).duracaoMinutos(30).build();

        when(profissionalRepository.findById(profissionalId)).thenReturn(Optional.of(profissional));
        when(servicoRepository.findById(servicoId)).thenReturn(Optional.of(servico));
        when(usuarioRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(consultarDisponibilidadeUseCase.estaDentroDeAlgumaJanela(profissionalId, dataHora)).thenReturn(true);
        when(agendamentoRepository.existsByProfissionalIdAndDataHora(profissionalId, dataHora)).thenReturn(false);
        when(agendamentoRepository.save(any(Agendamento.class))).thenAnswer(inv -> inv.getArgument(0));

        Agendamento resultado = useCase.executar(request, clienteId);

        assertThat(resultado.getClienteId()).isEqualTo(clienteId);
        assertThat(resultado.getProfissionalId()).isEqualTo(profissionalId);
        assertThat(resultado.getEstabelecimentoId()).isEqualTo(estabelecimentoId);
        verify(agendamentoRepository).save(any(Agendamento.class));
        verify(notificacaoService).enviarConfirmacao(any(Agendamento.class), eq("cliente@email.com"));
    }

    @Test
    @DisplayName("deve rejeitar quando horario esta fora da agenda de disponibilidade do profissional")
    void deveRejeitarForaDaDisponibilidade() {
        var profissional = Profissional.builder().id(profissionalId).build();
        var servico = Servico.builder().id(servicoId).estabelecimentoId(estabelecimentoId).build();

        when(profissionalRepository.findById(profissionalId)).thenReturn(Optional.of(profissional));
        when(servicoRepository.findById(servicoId)).thenReturn(Optional.of(servico));
        when(usuarioRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(consultarDisponibilidadeUseCase.estaDentroDeAlgumaJanela(profissionalId, dataHora)).thenReturn(false);

        assertThatThrownBy(() -> useCase.executar(request, clienteId))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("fora da agenda");

        verify(agendamentoRepository, never()).save(any());
        verifyNoInteractions(notificacaoService);
    }

    @Test
    @DisplayName("deve rejeitar double-booking quando profissional ja tem agendamento no horario (checagem em memoria)")
    void deveRejeitarDoubleBookingChecagemOtimista() {
        var profissional = Profissional.builder().id(profissionalId).build();
        var servico = Servico.builder().id(servicoId).estabelecimentoId(estabelecimentoId).build();

        when(profissionalRepository.findById(profissionalId)).thenReturn(Optional.of(profissional));
        when(servicoRepository.findById(servicoId)).thenReturn(Optional.of(servico));
        when(usuarioRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(consultarDisponibilidadeUseCase.estaDentroDeAlgumaJanela(profissionalId, dataHora)).thenReturn(true);
        when(agendamentoRepository.existsByProfissionalIdAndDataHora(profissionalId, dataHora)).thenReturn(true);

        assertThatThrownBy(() -> useCase.executar(request, clienteId))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("ja possui um agendamento");

        verify(agendamentoRepository, never()).save(any());
    }

    @Test
    @DisplayName("deve rejeitar double-booking vencido por corrida (constraint unica do banco)")
    void deveRejeitarDoubleBookingPorCorridaNoBanco() {
        var profissional = Profissional.builder().id(profissionalId).build();
        var servico = Servico.builder().id(servicoId).estabelecimentoId(estabelecimentoId).build();

        when(profissionalRepository.findById(profissionalId)).thenReturn(Optional.of(profissional));
        when(servicoRepository.findById(servicoId)).thenReturn(Optional.of(servico));
        when(usuarioRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(consultarDisponibilidadeUseCase.estaDentroDeAlgumaJanela(profissionalId, dataHora)).thenReturn(true);
        when(agendamentoRepository.existsByProfissionalIdAndDataHora(profissionalId, dataHora)).thenReturn(false);
        when(agendamentoRepository.save(any(Agendamento.class)))
                .thenThrow(new DataIntegrityViolationException("uk_profissional_data_hora"));

        assertThatThrownBy(() -> useCase.executar(request, clienteId))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("ja possui um agendamento");
    }

    @Test
    @DisplayName("deve lancar excecao quando profissional nao existe")
    void deveLancarExcecaoProfissionalNaoEncontrado() {
        when(profissionalRepository.findById(profissionalId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.executar(request, clienteId))
                .isInstanceOf(RecursoNaoEncontradoException.class);

        verifyNoInteractions(agendamentoRepository);
    }

    @Test
    @DisplayName("deve lancar excecao quando servico nao existe")
    void deveLancarExcecaoServicoNaoEncontrado() {
        var profissional = Profissional.builder().id(profissionalId).build();
        when(profissionalRepository.findById(profissionalId)).thenReturn(Optional.of(profissional));
        when(servicoRepository.findById(servicoId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.executar(request, clienteId))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }
}
