package br.com.fiap.agendamento.infrastructure.persistence.springdata;

import br.com.fiap.agendamento.infrastructure.persistence.entity.AvaliacaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/** Repositorio Spring Data para {@link AvaliacaoEntity}. Detalhe de infraestrutura, usado apenas pelo adapter. */
public interface AvaliacaoJpaRepository extends JpaRepository<AvaliacaoEntity, UUID> {
    boolean existsByAgendamentoId(UUID agendamentoId);
    List<AvaliacaoEntity> findByEstabelecimentoId(UUID estabelecimentoId);
    List<AvaliacaoEntity> findByProfissionalId(UUID profissionalId);
}
