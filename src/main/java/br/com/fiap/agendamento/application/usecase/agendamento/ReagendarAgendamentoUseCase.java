package br.com.fiap.agendamento.application.usecase.agendamento;

import br.com.fiap.agendamento.application.dto.ReagendarRequest;
import br.com.fiap.agendamento.application.port.NotificacaoService;
import br.com.fiap.agendamento.application.usecase.disponibilidade.ConsultarDisponibilidadeUseCase;
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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Caso de uso: reagendamento (feature 6 - painel do estabelecimento/cliente
 * ajustando a agenda). Reaplica as mesmas regras de disponibilidade e
 * double-booking usadas na criacao, sobre o novo horario.
 */
@Service
@RequiredArgsConstructor
public class ReagendarAgendamentoUseCase {

    private final AgendamentoRepository agendamentoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProfissionalRepository profissionalRepository;
    private final ConsultarDisponibilidadeUseCase consultarDisponibilidadeUseCase;
    private final NotificacaoService notificacaoService;

    @Transactional
    public Agendamento executar(UUID agendamentoId, UUID clienteId, ReagendarRequest request) {
        Agendamento agendamento = agendamentoRepository.findByIdAndClienteId(agendamentoId, clienteId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Agendamento nao encontrado: " + agendamentoId));

        if (!agendamento.podeCancelar()) {
            throw new RegraDeNegocioException(
                    "Agendamento com status " + agendamento.getStatus() + " nao pode ser reagendado");
        }

        if (!consultarDisponibilidadeUseCase.estaDentroDeAlgumaJanela(agendamento.getProfissionalId(), request.novaDataHora())) {
            throw new RegraDeNegocioException("Novo horario fora da agenda de disponibilidade do profissional");
        }

        boolean conflito = agendamentoRepository.existsByProfissionalIdAndDataHora(
                agendamento.getProfissionalId(), request.novaDataHora());
        if (conflito) {
            throw new RegraDeNegocioException("Profissional ja possui um agendamento neste novo horario");
        }

        agendamento.setDataHora(request.novaDataHora());
        agendamento.setStatus(StatusAgendamento.PENDENTE);
        agendamento.setLembreteEnviado(false);

        Agendamento salvo;
        try {
            salvo = agendamentoRepository.save(agendamento);
        } catch (DataIntegrityViolationException e) {
            throw new RegraDeNegocioException("Profissional ja possui um agendamento neste novo horario");
        }

        usuarioRepository.findById(clienteId)
                .ifPresent((Usuario cliente) -> notificacaoService.enviarConfirmacao(salvo, cliente.getEmail()));

        profissionalRepository.findById(salvo.getProfissionalId())
                .map(Profissional::getEmailContato)
                .filter(email -> email != null && !email.isBlank())
                .ifPresent(email -> notificacaoService.enviarConfirmacao(salvo, email));

        return salvo;
    }
}
