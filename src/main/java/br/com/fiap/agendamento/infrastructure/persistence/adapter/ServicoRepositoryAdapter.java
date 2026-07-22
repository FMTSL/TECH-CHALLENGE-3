package br.com.fiap.agendamento.infrastructure.persistence.adapter;

import br.com.fiap.agendamento.domain.model.Servico;
import br.com.fiap.agendamento.domain.repository.ServicoRepository;
import br.com.fiap.agendamento.infrastructure.persistence.mapper.ServicoMapper;
import br.com.fiap.agendamento.infrastructure.persistence.springdata.ServicoJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Implementacao do port {@link ServicoRepository} sobre Spring Data JPA. */
@Component
@RequiredArgsConstructor
public class ServicoRepositoryAdapter implements ServicoRepository {

    private final ServicoJpaRepository jpaRepository;

    @Override
    public Servico save(Servico servico) {
        var salvo = jpaRepository.save(ServicoMapper.toEntity(servico));
        return ServicoMapper.toDomain(salvo);
    }

    @Override
    public Optional<Servico> findById(UUID id) {
        return jpaRepository.findById(id).map(ServicoMapper::toDomain);
    }

    @Override
    public List<Servico> findAll() {
        return jpaRepository.findAll().stream().map(ServicoMapper::toDomain).toList();
    }

    @Override
    public List<Servico> findByEstabelecimentoId(UUID estabelecimentoId) {
        return jpaRepository.findByEstabelecimentoId(estabelecimentoId).stream()
                .map(ServicoMapper::toDomain).toList();
    }

    @Override
    public boolean existsByEstabelecimentoIdAndNomeContainingIgnoreCase(UUID estabelecimentoId, String nome) {
        return jpaRepository.existsByEstabelecimentoIdAndNomeContainingIgnoreCase(estabelecimentoId, nome);
    }

    @Override
    public boolean existsByEstabelecimentoIdAndPrecoBetween(UUID estabelecimentoId, BigDecimal precoMin, BigDecimal precoMax) {
        return jpaRepository.existsByEstabelecimentoIdAndPrecoBetween(estabelecimentoId, precoMin, precoMax);
    }
}
