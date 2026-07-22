package br.com.fiap.agendamento.infrastructure.persistence.springdata;

import br.com.fiap.agendamento.infrastructure.persistence.entity.ServicoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/** Repositorio Spring Data para {@link ServicoEntity}. Detalhe de infraestrutura, usado apenas pelo adapter. */
public interface ServicoJpaRepository extends JpaRepository<ServicoEntity, UUID> {
    List<ServicoEntity> findByEstabelecimentoId(UUID estabelecimentoId);
    boolean existsByEstabelecimentoIdAndNomeContainingIgnoreCase(UUID estabelecimentoId, String nome);
    boolean existsByEstabelecimentoIdAndPrecoBetween(UUID estabelecimentoId, BigDecimal precoMin, BigDecimal precoMax);
}
