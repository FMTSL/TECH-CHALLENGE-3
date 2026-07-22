package br.com.fiap.agendamento.infrastructure.persistence.adapter;

import br.com.fiap.agendamento.domain.model.DiaSemana;
import br.com.fiap.agendamento.domain.model.HorarioDisponivel;
import br.com.fiap.agendamento.domain.repository.HorarioDisponivelRepository;
import br.com.fiap.agendamento.infrastructure.persistence.mapper.HorarioDisponivelMapper;
import br.com.fiap.agendamento.infrastructure.persistence.springdata.HorarioDisponivelJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/** Implementacao do port {@link HorarioDisponivelRepository} sobre Spring Data JPA. */
@Component
@RequiredArgsConstructor
public class HorarioDisponivelRepositoryAdapter implements HorarioDisponivelRepository {

    private final HorarioDisponivelJpaRepository jpaRepository;

    @Override
    public HorarioDisponivel save(HorarioDisponivel horarioDisponivel) {
        var salvo = jpaRepository.save(HorarioDisponivelMapper.toEntity(horarioDisponivel));
        return HorarioDisponivelMapper.toDomain(salvo);
    }

    @Override
    public List<HorarioDisponivel> findByProfissionalId(UUID profissionalId) {
        return jpaRepository.findByProfissionalId(profissionalId).stream().map(HorarioDisponivelMapper::toDomain).toList();
    }

    @Override
    public List<HorarioDisponivel> findByProfissionalIdAndDiaSemana(UUID profissionalId, DiaSemana diaSemana) {
        return jpaRepository.findByProfissionalIdAndDiaSemana(profissionalId, diaSemana).stream()
                .map(HorarioDisponivelMapper::toDomain).toList();
    }
}
