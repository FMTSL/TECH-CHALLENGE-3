package br.com.fiap.agendamento.infrastructure.persistence.springdata;

import br.com.fiap.agendamento.domain.model.StatusAgendamento;
import br.com.fiap.agendamento.infrastructure.persistence.entity.AgendamentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Repositorio Spring Data para {@link AgendamentoEntity}. Detalhe de infraestrutura, usado apenas pelo adapter. */
public interface AgendamentoJpaRepository extends JpaRepository<AgendamentoEntity, UUID> {
    boolean existsByProfissionalIdAndDataHora(UUID profissionalId, LocalDateTime dataHora);
    List<AgendamentoEntity> findByClienteId(UUID clienteId);
    List<AgendamentoEntity> findByProfissionalId(UUID profissionalId);
    List<AgendamentoEntity> findByEstabelecimentoId(UUID estabelecimentoId);
    List<AgendamentoEntity> findByProfissionalIdAndDataHoraBetween(UUID profissionalId, LocalDateTime inicio, LocalDateTime fim);
    Optional<AgendamentoEntity> findByIdAndClienteId(UUID id, UUID clienteId);
    List<AgendamentoEntity> findByStatusAndLembreteEnviadoFalseAndDataHoraBetween(
            StatusAgendamento status, LocalDateTime inicio, LocalDateTime fim);
}
