package br.com.fiap.agendamento.application.usecase.agendamento;

import br.com.fiap.agendamento.application.port.NotificacaoService;
import br.com.fiap.agendamento.domain.exception.RecursoNaoEncontradoException;
import br.com.fiap.agendamento.domain.exception.RegraDeNegocioException;
import br.com.fiap.agendamento.domain.model.Agendamento;
import br.com.fiap.agendamento.domain.model.Profissional;
import br.com.fiap.agendamento.domain.model.StatusAgendamento;
import br.com.fiap.agendamento.domain.model.Usuario;
import br.com.fiap.agendamento.domain.repository.AgendamentoRepository;
import br.com.fiap.agendamento.domain.repository.ProfissionalRepository;
import br.com.fiap.agendamento.domain.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CancelarAgendamentoUseCase")
class CancelarAgendamentoUseCaseTest {

    @Mock private AgendamentoRepository agendamentoRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private ProfissionalRepository profissionalRepository;
    @Mock private NotificacaoService notificacaoService;

    @InjectMocks
    private CancelarAgendamentoUseCase useCase;

    @Test
    @DisplayName("deve cancelar agendamento PENDENTE e notificar o cliente e o profissional")
    void deveCancelarAgendamentoPendente() {
        UUID id = UUID.randomUUID();
        UUID clienteId = UUID.randomUUID();
        UUID profissionalId = UUID.randomUUID();
        var agendamento = Agendamento.builder().id(id).clienteId(clienteId).profissionalId(profissionalId)
                .dataHora(LocalDateTime.now().plusDays(2)).status(StatusAgendamento.PENDENTE).build();
        var cliente = Usuario.builder().id(clienteId).email("cliente@email.com").build();
        var profissional = Profissional.builder().id(profissionalId).emailContato("ana@salao.com").build();

        when(agendamentoRepository.findByIdAndClienteId(id, clienteId)).thenReturn(Optional.of(agendamento));
        when(agendamentoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(usuarioRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(profissionalRepository.findById(profissionalId)).thenReturn(Optional.of(profissional));

        Agendamento resultado = useCase.executar(id, clienteId);

        assertThat(resultado.getStatus()).isEqualTo(StatusAgendamento.CANCELADO);
        verify(agendamentoRepository).save(agendamento);
        verify(notificacaoService).enviarCancelamento(agendamento, "cliente@email.com");
        verify(notificacaoService).enviarCancelamento(agendamento, "ana@salao.com");
    }

    @ParameterizedTest
    @DisplayName("nao deve cancelar agendamento ja CANCELADO ou CONCLUIDO")
    @EnumSource(value = StatusAgendamento.class, names = {"CANCELADO", "CONCLUIDO", "NAO_COMPARECEU"})
    void naoDeveCancelarAgendamentoEmStatusFinal(StatusAgendamento status) {
        UUID id = UUID.randomUUID();
        UUID clienteId = UUID.randomUUID();
        var agendamento = Agendamento.builder().id(id).clienteId(clienteId).status(status).build();

        when(agendamentoRepository.findByIdAndClienteId(id, clienteId)).thenReturn(Optional.of(agendamento));

        assertThatThrownBy(() -> useCase.executar(id, clienteId))
                .isInstanceOf(RegraDeNegocioException.class);

        verifyNoInteractions(notificacaoService);
    }

    @Test
    @DisplayName("deve lancar excecao quando agendamento nao pertence ao cliente ou nao existe")
    void deveLancarExcecaoQuandoNaoEncontrado() {
        UUID id = UUID.randomUUID();
        UUID clienteId = UUID.randomUUID();
        when(agendamentoRepository.findByIdAndClienteId(id, clienteId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.executar(id, clienteId))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }
}
