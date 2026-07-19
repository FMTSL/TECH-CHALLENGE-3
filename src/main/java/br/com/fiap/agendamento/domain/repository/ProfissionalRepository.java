package br.com.fiap.agendamento.domain.repository;

import br.com.fiap.agendamento.domain.model.Profissional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/** Port de persistencia para {@link br.com.fiap.agendamento.domain.model.Profissional}. */
public interface ProfissionalRepository extends JpaRepository<Profissional, UUID> {
    List<Profissional> findByEstabelecimentoId(UUID estabelecimentoId);
}
