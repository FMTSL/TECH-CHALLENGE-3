package br.com.fiap.agendamento.infrastructure.persistence.adapter;

import br.com.fiap.agendamento.domain.model.Estabelecimento;
import br.com.fiap.agendamento.domain.repository.EstabelecimentoRepository;
import br.com.fiap.agendamento.infrastructure.persistence.mapper.EstabelecimentoMapper;
import br.com.fiap.agendamento.infrastructure.persistence.springdata.EstabelecimentoJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Implementacao do port {@link EstabelecimentoRepository} sobre Spring Data JPA. */
@Component
@RequiredArgsConstructor
public class EstabelecimentoRepositoryAdapter implements EstabelecimentoRepository {

    private final EstabelecimentoJpaRepository jpaRepository;

    @Override
    public Estabelecimento save(Estabelecimento estabelecimento) {
        var salvo = jpaRepository.save(EstabelecimentoMapper.toEntity(estabelecimento));
        return EstabelecimentoMapper.toDomain(salvo);
    }

    @Override
    public Optional<Estabelecimento> findById(UUID id) {
        return jpaRepository.findById(id).map(EstabelecimentoMapper::toDomain);
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public List<Estabelecimento> findAll() {
        return jpaRepository.findAll().stream().map(EstabelecimentoMapper::toDomain).toList();
    }

    @Override
    public List<Estabelecimento> findByNomeContainingIgnoreCaseOrCidadeContainingIgnoreCase(String nome, String cidade) {
        return jpaRepository.findByNomeContainingIgnoreCaseOrCidadeContainingIgnoreCase(nome, cidade).stream()
                .map(EstabelecimentoMapper::toDomain).toList();
    }

    @Override
    public List<Estabelecimento> findByCidadeIgnoreCase(String cidade) {
        return jpaRepository.findByCidadeIgnoreCase(cidade).stream().map(EstabelecimentoMapper::toDomain).toList();
    }
}
