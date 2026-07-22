package br.com.fiap.agendamento.domain.repository;

import br.com.fiap.agendamento.domain.model.Avaliacao;

import java.util.List;
import java.util.UUID;

/** Port de persistencia para {@link Avaliacao}. Interface pura de dominio, sem dependencia de JPA/Spring Data. */
public interface AvaliacaoRepository {
    Avaliacao save(Avaliacao avaliacao);
    boolean existsByAgendamentoId(UUID agendamentoId);
    List<Avaliacao> findByEstabelecimentoId(UUID estabelecimentoId);
    List<Avaliacao> findByProfissionalId(UUID profissionalId);
}
