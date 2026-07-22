package br.com.fiap.agendamento.infrastructure.persistence.springdata;

import br.com.fiap.agendamento.infrastructure.persistence.entity.ProfissionalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/** Repositorio Spring Data para {@link ProfissionalEntity}. Detalhe de infraestrutura, usado apenas pelo adapter. */
public interface ProfissionalJpaRepository extends JpaRepository<ProfissionalEntity, UUID> {
    List<ProfissionalEntity> findByEstabelecimentoId(UUID estabelecimentoId);
}
