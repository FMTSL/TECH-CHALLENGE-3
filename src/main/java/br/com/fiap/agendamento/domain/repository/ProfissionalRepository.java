package br.com.fiap.agendamento.domain.repository;

import br.com.fiap.agendamento.domain.model.Profissional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Port de persistencia para {@link Profissional}. Interface pura de dominio, sem dependencia de JPA/Spring Data. */
public interface ProfissionalRepository {
    Profissional save(Profissional profissional);
    Optional<Profissional> findById(UUID id);
    boolean existsById(UUID id);
    List<Profissional> findAll();
    List<Profissional> findByEstabelecimentoId(UUID estabelecimentoId);
}
