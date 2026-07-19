package br.com.fiap.agendamento.application.usecase.avaliacao;

import br.com.fiap.agendamento.application.dto.CriarAvaliacaoRequest;
import br.com.fiap.agendamento.domain.exception.RecursoNaoEncontradoException;
import br.com.fiap.agendamento.domain.exception.RegraDeNegocioException;
import br.com.fiap.agendamento.domain.model.Agendamento;
import br.com.fiap.agendamento.domain.model.Avaliacao;
import br.com.fiap.agendamento.domain.model.StatusAgendamento;
import br.com.fiap.agendamento.domain.repository.AgendamentoRepository;
import br.com.fiap.agendamento.domain.repository.AvaliacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Caso de uso: cliente avalia um atendimento apos o servico ser concluido (feature 4).
 * Regras: so pode avaliar agendamento proprio, ja concluido, e uma unica vez.
 */
@Service
@RequiredArgsConstructor
public class CriarAvaliacaoUseCase {

    private final AvaliacaoRepository avaliacaoRepository;
    private final AgendamentoRepository agendamentoRepository;

    @Transactional
    public Avaliacao executar(CriarAvaliacaoRequest request, UUID clienteId) {
        Agendamento agendamento = agendamentoRepository.findByIdAndClienteId(request.agendamentoId(), clienteId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Agendamento nao encontrado: " + request.agendamentoId()));

        if (agendamento.getStatus() != StatusAgendamento.CONCLUIDO) {
            throw new RegraDeNegocioException("So e possivel avaliar agendamentos concluidos");
        }

        if (avaliacaoRepository.existsByAgendamentoId(agendamento.getId())) {
            throw new RegraDeNegocioException("Este agendamento ja foi avaliado");
        }

        Avaliacao avaliacao = Avaliacao.builder()
                .agendamentoId(agendamento.getId())
                .clienteId(clienteId)
                .estabelecimentoId(agendamento.getEstabelecimentoId())
                .profissionalId(agendamento.getProfissionalId())
                .nota(request.nota())
                .comentario(request.comentario())
                .build();

        return avaliacaoRepository.save(avaliacao);
    }
}
