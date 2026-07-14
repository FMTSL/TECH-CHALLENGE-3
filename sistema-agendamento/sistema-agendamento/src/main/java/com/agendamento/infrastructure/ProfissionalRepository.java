package com.agendamento.infrastructure;

import com.agendamento.domain.Profissional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfissionalRepository extends JpaRepository<Profissional, Long> {
    List<Profissional> findByEspecialidadeContainingIgnoreCase(String especialidade);
    List<Profissional> findByEstabelecimentoId(Long estabelecimentoId);
    List<Profissional> findByDisponibilidadeContainingIgnoreCase(String disponibilidade);
    List<Profissional> findByTarifaBetween(Double min, Double max);
}