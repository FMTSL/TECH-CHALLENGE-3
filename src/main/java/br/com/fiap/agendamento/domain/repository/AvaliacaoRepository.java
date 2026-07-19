package br.com.fiap.agendamento.domain.repository;

import br.com.fiap.agendamento.domain.model.Avaliacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/** Port de persistencia para {@link br.com.fiap.agendamento.domain.model.Avaliacao}. */
public interface AvaliacaoRepository extends JpaRepository<Avaliacao, UUID> {
    boolean existsByAgendamentoId(UUID agendamentoId);
    List<Avaliacao> findByEstabelecimentoId(UUID estabelecimentoId);
    List<Avaliacao> findByProfissionalId(UUID profissionalId);
}
