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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/** Caso de uso: cliente cancela um agendamento proprio ainda pendente/confirmado, notificando cliente e profissional. */
@Service
@RequiredArgsConstructor
public class CancelarAgendamentoUseCase {

    private final AgendamentoRepository agendamentoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProfissionalRepository profissionalRepository;
    private final NotificacaoService notificacaoService;

    @Transactional
    public Agendamento executar(UUID agendamentoId, UUID clienteId) {
        Agendamento agendamento = agendamentoRepository.findByIdAndClienteId(agendamentoId, clienteId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Agendamento nao encontrado: " + agendamentoId));

        if (!agendamento.podeCancelar()) {
            throw new RegraDeNegocioException(
                    "Agendamento com status " + agendamento.getStatus() + " nao pode ser cancelado");
        }

        agendamento.setStatus(StatusAgendamento.CANCELADO);
        Agendamento salvo = agendamentoRepository.save(agendamento);

        usuarioRepository.findById(clienteId)
                .ifPresent((Usuario cliente) -> notificacaoService.enviarCancelamento(salvo, cliente.getEmail()));

        profissionalRepository.findById(salvo.getProfissionalId())
                .map(Profissional::getEmailContato)
                .filter(email -> email != null && !email.isBlank())
                .ifPresent(email -> notificacaoService.enviarCancelamento(salvo, email));

        return salvo;
    }
}
