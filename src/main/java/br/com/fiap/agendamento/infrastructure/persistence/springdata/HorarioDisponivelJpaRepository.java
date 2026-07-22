package br.com.fiap.agendamento.infrastructure.persistence.springdata;

import br.com.fiap.agendamento.domain.model.DiaSemana;
import br.com.fiap.agendamento.infrastructure.persistence.entity.HorarioDisponivelEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/** Repositorio Spring Data para {@link HorarioDisponivelEntity}. Detalhe de infraestrutura, usado apenas pelo adapter. */
public interface HorarioDisponivelJpaRepository extends JpaRepository<HorarioDisponivelEntity, UUID> {
    List<HorarioDisponivelEntity> findByProfissionalId(UUID profissionalId);
    List<HorarioDisponivelEntity> findByProfissionalIdAndDiaSemana(UUID profissionalId, DiaSemana diaSemana);
}
