package br.com.fiap.agendamento.infrastructure.persistence.mapper;

import br.com.fiap.agendamento.domain.model.Profissional;
import br.com.fiap.agendamento.infrastructure.persistence.entity.ProfissionalEntity;

import java.util.ArrayList;

/** Converte entre {@link Profissional} (dominio) e {@link ProfissionalEntity} (persistencia JPA). */
public final class ProfissionalMapper {

    private ProfissionalMapper() {}

    public static ProfissionalEntity toEntity(Profissional p) {
        if (p == null) {
            return null;
        }
        return ProfissionalEntity.builder()
                .id(p.getId())
                .nome(p.getNome())
                .especialidades(p.getEspecialidades() != null ? new ArrayList<>(p.getEspecialidades()) : new ArrayList<>())
                .tarifaBase(p.getTarifaBase())
                .estabelecimentoId(p.getEstabelecimentoId())
                .emailContato(p.getEmailContato())
                .build();
    }

    public static Profissional toDomain(ProfissionalEntity entity) {
        if (entity == null) {
            return null;
        }
        return Profissional.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .especialidades(entity.getEspecialidades() != null ? new ArrayList<>(entity.getEspecialidades()) : new ArrayList<>())
                .tarifaBase(entity.getTarifaBase())
                .estabelecimentoId(entity.getEstabelecimentoId())
                .emailContato(entity.getEmailContato())
                .build();
    }
}
