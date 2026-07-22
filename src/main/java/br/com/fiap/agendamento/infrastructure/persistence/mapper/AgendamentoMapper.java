package br.com.fiap.agendamento.infrastructure.persistence.mapper;

import br.com.fiap.agendamento.domain.model.Agendamento;
import br.com.fiap.agendamento.infrastructure.persistence.entity.AgendamentoEntity;

/** Converte entre {@link Agendamento} (dominio) e {@link AgendamentoEntity} (persistencia JPA). */
public final class AgendamentoMapper {

    private AgendamentoMapper() {}

    public static AgendamentoEntity toEntity(Agendamento a) {
        if (a == null) {
            return null;
        }
        return AgendamentoEntity.builder()
                .id(a.getId())
                .clienteId(a.getClienteId())
                .profissionalId(a.getProfissionalId())
                .servicoId(a.getServicoId())
                .estabelecimentoId(a.getEstabelecimentoId())
                .dataHora(a.getDataHora())
                .status(a.getStatus())
                .criadoEm(a.getCriadoEm())
                .lembreteEnviado(a.isLembreteEnviado())
                .build();
    }

    public static Agendamento toDomain(AgendamentoEntity entity) {
        if (entity == null) {
            return null;
        }
        return Agendamento.builder()
                .id(entity.getId())
                .clienteId(entity.getClienteId())
                .profissionalId(entity.getProfissionalId())
                .servicoId(entity.getServicoId())
                .estabelecimentoId(entity.getEstabelecimentoId())
                .dataHora(entity.getDataHora())
                .status(entity.getStatus())
                .criadoEm(entity.getCriadoEm())
                .lembreteEnviado(entity.isLembreteEnviado())
                .build();
    }
}
