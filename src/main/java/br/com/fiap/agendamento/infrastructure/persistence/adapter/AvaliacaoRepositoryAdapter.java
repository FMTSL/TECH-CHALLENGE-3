package br.com.fiap.agendamento.infrastructure.persistence.adapter;

import br.com.fiap.agendamento.domain.model.Avaliacao;
import br.com.fiap.agendamento.domain.repository.AvaliacaoRepository;
import br.com.fiap.agendamento.infrastructure.persistence.mapper.AvaliacaoMapper;
import br.com.fiap.agendamento.infrastructure.persistence.springdata.AvaliacaoJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/** Implementacao do port {@link AvaliacaoRepository} sobre Spring Data JPA. */
@Component
@RequiredArgsConstructor
public class AvaliacaoRepositoryAdapter implements AvaliacaoRepository {

    private final AvaliacaoJpaRepository jpaRepository;

    @Override
    public Avaliacao save(Avaliacao avaliacao) {
        var salvo = jpaRepository.save(AvaliacaoMapper.toEntity(avaliacao));
        return AvaliacaoMapper.toDomain(salvo);
    }

    @Override
    public boolean existsByAgendamentoId(UUID agendamentoId) {
        return jpaRepository.existsByAgendamentoId(agendamentoId);
    }

    @Override
    public List<Avaliacao> findByEstabelecimentoId(UUID estabelecimentoId) {
        return jpaRepository.findByEstabelecimentoId(estabelecimentoId).stream().map(AvaliacaoMapper::toDomain).toList();
    }

    @Override
    public List<Avaliacao> findByProfissionalId(UUID profissionalId) {
        return jpaRepository.findByProfissionalId(profissionalId).stream().map(AvaliacaoMapper::toDomain).toList();
    }
}
