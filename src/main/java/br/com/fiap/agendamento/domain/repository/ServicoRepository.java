package br.com.fiap.agendamento.domain.repository;

import br.com.fiap.agendamento.domain.model.Servico;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Port de persistencia para {@link Servico}. Interface pura de dominio, sem dependencia de JPA/Spring Data. */
public interface ServicoRepository {
    Servico save(Servico servico);
    Optional<Servico> findById(UUID id);
    List<Servico> findAll();
    List<Servico> findByEstabelecimentoId(UUID estabelecimentoId);
    boolean existsByEstabelecimentoIdAndNomeContainingIgnoreCase(UUID estabelecimentoId, String nome);
    boolean existsByEstabelecimentoIdAndPrecoBetween(UUID estabelecimentoId, BigDecimal precoMin, BigDecimal precoMax);
}
