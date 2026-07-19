package br.com.fiap.agendamento.domain.repository;

import br.com.fiap.agendamento.domain.model.Agendamento;
import br.com.fiap.agendamento.domain.model.StatusAgendamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Port de persistencia para {@link Agendamento} — o agregado central do sistema (double-booking, agenda, lembretes). */
public interface AgendamentoRepository extends JpaRepository<Agendamento, UUID> {
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
