package br.com.fiap.agendamento.infrastructure.persistence.mapper;

import br.com.fiap.agendamento.domain.model.Servico;
import br.com.fiap.agendamento.infrastructure.persistence.entity.ServicoEntity;

/** Converte entre {@link Servico} (dominio) e {@link ServicoEntity} (persistencia JPA). */
public final class ServicoMapper {

    private ServicoMapper() {}

    public static ServicoEntity toEntity(Servico s) {
        if (s == null) {
            return null;
        }
        return ServicoEntity.builder()
                .id(s.getId())
                .nome(s.getNome())
                .descricao(s.getDescricao())
                .preco(s.getPreco())
                .duracaoMinutos(s.getDuracaoMinutos())
                .estabelecimentoId(s.getEstabelecimentoId())
                .build();
    }

    public static Servico toDomain(ServicoEntity entity) {
        if (entity == null) {
            return null;
        }
        return Servico.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .descricao(entity.getDescricao())
                .preco(entity.getPreco())
                .duracaoMinutos(entity.getDuracaoMinutos())
                .estabelecimentoId(entity.getEstabelecimentoId())
                .build();
    }
}
