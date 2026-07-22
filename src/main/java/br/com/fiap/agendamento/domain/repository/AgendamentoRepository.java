package br.com.fiap.agendamento.domain.repository;

import br.com.fiap.agendamento.domain.model.Agendamento;
import br.com.fiap.agendamento.domain.model.StatusAgendamento;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Port de persistencia para {@link Agendamento} — o agregado central do sistema
 * (double-booking, agenda, lembretes). Interface pura de dominio, sem dependencia
 * de JPA/Spring Data; a implementacao concreta vive em infrastructure.persistence.adapter.
 */
public interface AgendamentoRepository {
    Agendamento save(Agendamento agendamento);
    Optional<Agendamento> findById(UUID id);
    boolean existsByProfissionalIdAndDataHora(UUID profissionalId, LocalDateTime dataHora);
    List<Agendamento> findByClienteId(UUID clienteId);
    List<Agendamento> findByProfissionalId(UUID profissionalId);
    List<Agendamento> findByEstabelecimentoId(UUID estabelecimentoId);
    List<Agendamento> findByProfissionalIdAndDataHoraBetween(UUID profissionalId, LocalDateTime inicio, LocalDateTime fim);
    Optional<Agendamento> findByIdAndClienteId(UUID id, UUID clienteId);

    /** Usado pelo scheduler de lembretes: agendamentos confirmados no intervalo, ainda sem lembrete disparado. */
    List<Agendamento> findByStatusAndLembreteEnviadoFalseAndDataHoraBetween(
            StatusAgendamento status, LocalDateTime inicio, LocalDateTime fim);
}
