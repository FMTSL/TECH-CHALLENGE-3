package br.com.fiap.agendamento.application.usecase.agendamento;

import br.com.fiap.agendamento.application.dto.ReagendarRequest;
import br.com.fiap.agendamento.application.port.NotificacaoService;
import br.com.fiap.agendamento.application.usecase.disponibilidade.ConsultarDisponibilidadeUseCase;
import br.com.fiap.agendamento.domain.exception.RecursoNaoEncontradoException;
import br.com.fiap.agendamento.domain.exception.RegraDeNegocioException;
import br.com.fiap.agendamento.domain.model.Agendamento;
import br.com.fiap.agendamento.domain.model.StatusAgendamento;
import br.com.fiap.agendamento.domain.model.Usuario;
import br.com.fiap.agendamento.domain.repository.AgendamentoRepository;
import br.com.fiap.agendamento.domain.repository.ProfissionalRepository;
import br.com.fiap.agendamento.domain.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReagendarAgendamentoUseCase")
class ReagendarAgendamentoUseCaseTest {

    @Mock private AgendamentoRepository agendamentoRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private ProfissionalRepository profissionalRepository;
    @Mock private ConsultarDisponibilidadeUseCase consultarDisponibilidadeUseCase;
    @Mock private NotificacaoService notificacaoService;

    @InjectMocks
    private ReagendarAgendamentoUseCase useCase;

    private UUID agendamentoId;
    private UUID clienteId;
    private UUID profissionalId;
    private LocalDateTime novaDataHora;
    private ReagendarRequest request;
    private Agendamento agendamento;

    @BeforeEach
    void setUp() {
        agendamentoId = UUID.randomUUID();
        clienteId = UUID.randomUUID();
        profissionalId = UUID.randomUUID();
        novaDataHora = LocalDateTime.now().plusDays(3);
        request = new ReagendarRequest(novaDataHora);
        agendamento = Agendamento.builder().id(agendamentoId).clienteId(clienteId).profissionalId(profissionalId)
                .dataHora(LocalDateTime.now().plusDays(1)).status(StatusAgendamento.CONFIRMADO)
                .lembreteEnviado(true).build();
    }

    @Test
    @DisplayName("deve reagendar para novo horario valido, resetando status e flag de lembrete")
    void deveReagendarComSucesso() {
        var cliente = Usuario.builder().id(clienteId).email("cliente@email.com").build();

        when(agendamentoRepository.findByIdAndClienteId(agendamentoId, clienteId)).thenReturn(Optional.of(agendamento));
        when(consultarDisponibilidadeUseCase.estaDentroDeAlgumaJanela(profissionalId, novaDataHora)).thenReturn(true);
        when(agendamentoRepository.existsByProfissionalIdAndDataHora(profissionalId, novaDataHora)).thenReturn(false);
        when(agendamentoRepository.save(any(Agendamento.class))).thenAnswer(inv -> inv.getArgument(0));
        when(usuarioRepository.findById(clienteId)).thenReturn(Optional.of(cliente));

        Agendamento resultado = useCase.executar(agendamentoId, clienteId, request);

        assertThat(resultado.getDataHora()).isEqualTo(novaDataHora);
        assertThat(resultado.getStatus()).isEqualTo(StatusAgendamento.PENDENTE);
        assertThat(resultado.isLembreteEnviado()).isFalse();
        verify(notificacaoService).enviarConfirmacao(resultado, "cliente@email.com");
    }

    @Test
    @DisplayName("deve rejeitar reagendamento de agendamento ja cancelado")
    void deveRejeitarAgendamentoCancelado() {
        agendamento.setStatus(StatusAgendamento.CANCELADO);
        when(agendamentoRepository.findByIdAndClienteId(agendamentoId, clienteId)).thenReturn(Optional.of(agendamento));

        assertThatThrownBy(() -> useCase.executar(agendamentoId, clienteId, request))
                .isInstanceOf(RegraDeNegocioException.class);

        verify(agendamentoRepository, never()).save(any());
    }

    @Test
    @DisplayName("deve rejeitar quando novo horario esta fora da agenda do profissional")
    void deveRejeitarForaDaAgenda() {
        when(agendamentoRepository.findByIdAndClienteId(agendamentoId, clienteId)).thenReturn(Optional.of(agendamento));
        when(consultarDisponibilidadeUseCase.estaDentroDeAlgumaJanela(profissionalId, novaDataHora)).thenReturn(false);

        assertThatThrownBy(() -> useCase.executar(agendamentoId, clienteId, request))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("fora da agenda");
    }

    @Test
    @DisplayName("deve rejeitar quando o novo horario ja esta ocupado (double-booking)")
    void deveRejeitarNovoHorarioOcupado() {
        when(agendamentoRepository.findByIdAndClienteId(agendamentoId, clienteId)).thenReturn(Optional.of(agendamento));
        when(consultarDisponibilidadeUseCase.estaDentroDeAlgumaJanela(profissionalId, novaDataHora)).thenReturn(true);
        when(agendamentoRepository.existsByProfissionalIdAndDataHora(profissionalId, novaDataHora)).thenReturn(true);

        assertThatThrownBy(() -> useCase.executar(agendamentoId, clienteId, request))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("ja possui um agendamento");
    }

    @Test
    @DisplayName("deve traduzir corrida no banco (constraint unica) em RegraDeNegocioException")
    void deveTraduzirCorridaNoBanco() {
        when(agendamentoRepository.findByIdAndClienteId(agendamentoId, clienteId)).thenReturn(Optional.of(agendamento));
        when(consultarDisponibilidadeUseCase.estaDentroDeAlgumaJanela(profissionalId, novaDataHora)).thenReturn(true);
        when(agendamentoRepository.existsByProfissionalIdAndDataHora(profissionalId, novaDataHora)).thenReturn(false);
        when(agendamentoRepository.save(any(Agendamento.class)))
                .thenThrow(new DataIntegrityViolationException("uk_profissional_data_hora"));

        assertThatThrownBy(() -> useCase.executar(agendamentoId, clienteId, request))
                .isInstanceOf(RegraDeNegocioException.class);
    }

    @Test
    @DisplayName("deve lancar excecao quando agendamento nao encontrado")
    void deveLancarExcecaoQuandoNaoEncontrado() {
        when(agendamentoRepository.findByIdAndClienteId(agendamentoId, clienteId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.executar(agendamentoId, clienteId, request))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }
}
