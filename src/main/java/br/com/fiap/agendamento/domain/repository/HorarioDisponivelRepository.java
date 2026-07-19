package br.com.fiap.agendamento.domain.repository;

import br.com.fiap.agendamento.domain.model.DiaSemana;
import br.com.fiap.agendamento.domain.model.HorarioDisponivel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/** Port de persistencia para {@link br.com.fiap.agendamento.domain.model.HorarioDisponivel}. */
public interface HorarioDisponivelRepository extends JpaRepository<HorarioDisponivel, UUID> {
    List<HorarioDisponivel> findByProfissionalId(UUID profissionalId);
    List<HorarioDisponivel> findByProfissionalIdAndDiaSemana(UUID profissionalId, DiaSemana diaSemana);
}
