package br.com.fiap.agendamento.application.usecase.agendamento;

import br.com.fiap.agendamento.domain.exception.RecursoNaoEncontradoException;
import br.com.fiap.agendamento.domain.model.Agendamento;
import br.com.fiap.agendamento.domain.model.StatusAgendamento;
import br.com.fiap.agendamento.domain.repository.AgendamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Usado pelo painel do estabelecimento (feature 6): confirmar, marcar como concluido
 * ou marcar nao-comparecimento.
 */
@Service
@RequiredArgsConstructor
public class AtualizarStatusAgendamentoUseCase {

    private final AgendamentoRepository agendamentoRepository;

    @Transactional
    public Agendamento executar(UUID agendamentoId, StatusAgendamento novoStatus) {
        Agendamento agendamento = agendamentoRepository.findById(agendamentoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Agendamento nao encontrado: " + agendamentoId));

        agendamento.setStatus(novoStatus);
        return agendamentoRepository.save(agendamento);
    }
}
