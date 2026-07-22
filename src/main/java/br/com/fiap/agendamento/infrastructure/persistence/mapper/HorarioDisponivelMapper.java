package br.com.fiap.agendamento.infrastructure.persistence.mapper;

import br.com.fiap.agendamento.domain.model.HorarioDisponivel;
import br.com.fiap.agendamento.infrastructure.persistence.entity.HorarioDisponivelEntity;

/** Converte entre {@link HorarioDisponivel} (dominio) e {@link HorarioDisponivelEntity} (persistencia JPA). */
public final class HorarioDisponivelMapper {

    private HorarioDisponivelMapper() {}

    public static HorarioDisponivelEntity toEntity(HorarioDisponivel h) {
        if (h == null) {
            return null;
        }
        return HorarioDisponivelEntity.builder()
                .id(h.getId())
                .profissionalId(h.getProfissionalId())
                .diaSemana(h.getDiaSemana())
                .horaInicio(h.getHoraInicio())
                .horaFim(h.getHoraFim())
                .build();
    }

    public static HorarioDisponivel toDomain(HorarioDisponivelEntity entity) {
        if (entity == null) {
            return null;
        }
        return HorarioDisponivel.builder()
                .id(entity.getId())
                .profissionalId(entity.getProfissionalId())
                .diaSemana(entity.getDiaSemana())
                .horaInicio(entity.getHoraInicio())
                .horaFim(entity.getHoraFim())
                .build();
    }
}
