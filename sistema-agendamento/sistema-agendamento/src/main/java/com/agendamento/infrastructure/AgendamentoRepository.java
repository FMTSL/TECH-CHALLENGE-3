package com.agendamento.infrastructure;

import com.agendamento.domain.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {
    List<Agendamento> findByProfissionalIdAndDataHora(Long profissionalId, LocalDateTime dataHora);
    List<Agendamento> findByEstabelecimentoId(Long estabelecimentoId);
    List<Agendamento> findByProfissionalId(Long profissionalId);

}