package br.com.fiap.agendamento.infrastructure.persistence.adapter;

import br.com.fiap.agendamento.domain.model.Profissional;
import br.com.fiap.agendamento.domain.repository.ProfissionalRepository;
import br.com.fiap.agendamento.infrastructure.persistence.mapper.ProfissionalMapper;
import br.com.fiap.agendamento.infrastructure.persistence.springdata.ProfissionalJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Implementacao do port {@link ProfissionalRepository} sobre Spring Data JPA. */
@Component
@RequiredArgsConstructor
public class ProfissionalRepositoryAdapter implements ProfissionalRepository {

    private final ProfissionalJpaRepository jpaRepository;

    @Override
    public Profissional save(Profissional profissional) {
        var salvo = jpaRepository.save(ProfissionalMapper.toEntity(profissional));
        return ProfissionalMapper.toDomain(salvo);
    }

    @Override
    public Optional<Profissional> findById(UUID id) {
        return jpaRepository.findById(id).map(ProfissionalMapper::toDomain);
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public List<Profissional> findAll() {
        return jpaRepository.findAll().stream().map(ProfissionalMapper::toDomain).toList();
    }

    @Override
    public List<Profissional> findByEstabelecimentoId(UUID estabelecimentoId) {
        return jpaRepository.findByEstabelecimentoId(estabelecimentoId).stream()
                .map(ProfissionalMapper::toDomain).toList();
    }
}
