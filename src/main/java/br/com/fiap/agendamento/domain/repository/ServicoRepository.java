package br.com.fiap.agendamento.domain.repository;

import br.com.fiap.agendamento.domain.model.Servico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/** Port de persistencia para {@link br.com.fiap.agendamento.domain.model.Servico}. */
public interface ServicoRepository extends JpaRepository<Servico, UUID> {
    List<Servico> findByEstabelecimentoId(UUID estabelecimentoId);
    boolean existsByEstabelecimentoIdAndNomeContainingIgnoreCase(UUID estabelecimentoId, String nome);
    boolean existsByEstabelecimentoIdAndPrecoBetween(UUID estabelecimentoId, BigDecimal precoMin, BigDecimal precoMax);
}
