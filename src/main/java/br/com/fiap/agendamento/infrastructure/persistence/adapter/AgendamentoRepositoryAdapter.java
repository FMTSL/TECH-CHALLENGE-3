package br.com.fiap.agendamento.infrastructure.persistence.adapter;

import br.com.fiap.agendamento.domain.model.Agendamento;
import br.com.fiap.agendamento.domain.model.StatusAgendamento;
import br.com.fiap.agendamento.domain.repository.AgendamentoRepository;
import br.com.fiap.agendamento.infrastructure.persistence.mapper.AgendamentoMapper;
import br.com.fiap.agendamento.infrastructure.persistence.springdata.AgendamentoJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementacao do port {@link AgendamentoRepository} sobre Spring Data JPA.
 * A constraint unica (profissional_id, data_hora) mapeada em {@code AgendamentoEntity}
 * continua ativa normalmente: uma violacao lancada pelo Hibernate em {@code save()}
 * (DataIntegrityViolationException) se propaga sem alteracao para quem chamou este
 * adapter, permitindo que o use case a traduza para uma excecao de dominio.
 */
@Component
@RequiredArgsConstructor
public class AgendamentoRepositoryAdapter implements AgendamentoRepository {

    private final AgendamentoJpaRepository jpaRepository;

    @Override
    public Agendamento save(Agendamento agendamento) {
        var salvo = jpaRepository.save(AgendamentoMapper.toEntity(agendamento));
        return AgendamentoMapper.toDomain(salvo);
    }

    @Override
    public Optional<Agendamento> findById(UUID id) {
        return jpaRepository.findById(id).map(AgendamentoMapper::toDomain);
    }

    @Override
    public boolean existsByProfissionalIdAndDataHora(UUID profissionalId, LocalDateTime dataHora) {
        return jpaRepository.existsByProfissionalIdAndDataHora(profissionalId, dataHora);
    }

    @Override
    public List<Agendamento> findByClienteId(UUID clienteId) {
        return jpaRepository.findByClienteId(clienteId).stream().map(AgendamentoMapper::toDomain).toList();
    }

    @Override
    public List<Agendamento> findByProfissionalId(UUID profissionalId) {
        return jpaRepository.findByProfissionalId(profissionalId).stream().map(AgendamentoMapper::toDomain).toList();
    }

    @Override
    public List<Agendamento> findByEstabelecimentoId(UUID estabelecimentoId) {
        return jpaRepository.findByEstabelecimentoId(estabelecimentoId).stream().map(AgendamentoMapper::toDomain).toList();
    }

    @Override
    public List<Agendamento> findByProfissionalIdAndDataHoraBetween(UUID profissionalId, LocalDateTime inicio, LocalDateTime fim) {
        return jpaRepository.findByProfissionalIdAndDataHoraBetween(profissionalId, inicio, fim).stream()
                .map(AgendamentoMapper::toDomain).toList();
    }

    @Override
    public Optional<Agendamento> findByIdAndClienteId(UUID id, UUID clienteId) {
        return jpaRepository.findByIdAndClienteId(id, clienteId).map(AgendamentoMapper::toDomain);
    }

    @Override
    public List<Agendamento> findByStatusAndLembreteEnviadoFalseAndDataHoraBetween(
            StatusAgendamento status, LocalDateTime inicio, LocalDateTime fim) {
        return jpaRepository.findByStatusAndLembreteEnviadoFalseAndDataHoraBetween(status, inicio, fim).stream()
                .map(AgendamentoMapper::toDomain).toList();
    }
}
