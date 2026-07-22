package br.com.fiap.agendamento.infrastructure.persistence.springdata;

import br.com.fiap.agendamento.infrastructure.persistence.entity.EstabelecimentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/** Repositorio Spring Data para {@link EstabelecimentoEntity}. Detalhe de infraestrutura, usado apenas pelo adapter. */
public interface EstabelecimentoJpaRepository extends JpaRepository<EstabelecimentoEntity, UUID> {
    List<EstabelecimentoEntity> findByNomeContainingIgnoreCaseOrCidadeContainingIgnoreCase(String nome, String cidade);
    List<EstabelecimentoEntity> findByCidadeIgnoreCase(String cidade);
}
