package br.com.fiap.agendamento.domain.repository;

import br.com.fiap.agendamento.domain.model.DiaSemana;
import br.com.fiap.agendamento.domain.model.HorarioDisponivel;

import java.util.List;
import java.util.UUID;

/** Port de persistencia para {@link HorarioDisponivel}. Interface pura de dominio, sem dependencia de JPA/Spring Data. */
public interface HorarioDisponivelRepository {
    HorarioDisponivel save(HorarioDisponivel horarioDisponivel);
    List<HorarioDisponivel> findByProfissionalId(UUID profissionalId);
    List<HorarioDisponivel> findByProfissionalIdAndDiaSemana(UUID profissionalId, DiaSemana diaSemana);
}
