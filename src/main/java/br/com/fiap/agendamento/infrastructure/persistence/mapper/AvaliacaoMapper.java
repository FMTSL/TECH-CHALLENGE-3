package br.com.fiap.agendamento.infrastructure.persistence.mapper;

import br.com.fiap.agendamento.domain.model.Avaliacao;
import br.com.fiap.agendamento.infrastructure.persistence.entity.AvaliacaoEntity;

/** Converte entre {@link Avaliacao} (dominio) e {@link AvaliacaoEntity} (persistencia JPA). */
public final class AvaliacaoMapper {

    private AvaliacaoMapper() {}

    public static AvaliacaoEntity toEntity(Avaliacao a) {
        if (a == null) {
            return null;
        }
        return AvaliacaoEntity.builder()
                .id(a.getId())
                .agendamentoId(a.getAgendamentoId())
                .clienteId(a.getClienteId())
                .estabelecimentoId(a.getEstabelecimentoId())
                .profissionalId(a.getProfissionalId())
                .nota(a.getNota())
                .comentario(a.getComentario())
                .criadoEm(a.getCriadoEm())
                .build();
    }

    public static Avaliacao toDomain(AvaliacaoEntity entity) {
        if (entity == null) {
            return null;
        }
        return Avaliacao.builder()
                .id(entity.getId())
                .agendamentoId(entity.getAgendamentoId())
                .clienteId(entity.getClienteId())
                .estabelecimentoId(entity.getEstabelecimentoId())
                .profissionalId(entity.getProfissionalId())
                .nota(entity.getNota())
                .comentario(entity.getComentario())
                .criadoEm(entity.getCriadoEm())
                .build();
    }
}
