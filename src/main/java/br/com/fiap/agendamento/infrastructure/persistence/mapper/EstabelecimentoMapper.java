package br.com.fiap.agendamento.infrastructure.persistence.mapper;

import br.com.fiap.agendamento.domain.model.Estabelecimento;
import br.com.fiap.agendamento.infrastructure.persistence.entity.EstabelecimentoEntity;

import java.util.ArrayList;

/** Converte entre {@link Estabelecimento} (dominio) e {@link EstabelecimentoEntity} (persistencia JPA). */
public final class EstabelecimentoMapper {

    private EstabelecimentoMapper() {}

    public static EstabelecimentoEntity toEntity(Estabelecimento e) {
        if (e == null) {
            return null;
        }
        return EstabelecimentoEntity.builder()
                .id(e.getId())
                .nome(e.getNome())
                .endereco(e.getEndereco())
                .cidade(e.getCidade())
                .horarioFuncionamento(e.getHorarioFuncionamento())
                .fotos(e.getFotos() != null ? new ArrayList<>(e.getFotos()) : new ArrayList<>())
                .usuarioDonoId(e.getUsuarioDonoId())
                .criadoEm(e.getCriadoEm())
                .build();
    }

    public static Estabelecimento toDomain(EstabelecimentoEntity entity) {
        if (entity == null) {
            return null;
        }
        return Estabelecimento.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .endereco(entity.getEndereco())
                .cidade(entity.getCidade())
                .horarioFuncionamento(entity.getHorarioFuncionamento())
                .fotos(entity.getFotos() != null ? new ArrayList<>(entity.getFotos()) : new ArrayList<>())
                .usuarioDonoId(entity.getUsuarioDonoId())
                .criadoEm(entity.getCriadoEm())
                .build();
    }
}
