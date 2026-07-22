package br.com.fiap.agendamento.infrastructure.persistence.mapper;

import br.com.fiap.agendamento.domain.model.Usuario;
import br.com.fiap.agendamento.infrastructure.persistence.entity.UsuarioEntity;

/** Converte entre {@link Usuario} (dominio) e {@link UsuarioEntity} (persistencia JPA). */
public final class UsuarioMapper {

    private UsuarioMapper() {}

    public static UsuarioEntity toEntity(Usuario usuario) {
        if (usuario == null) {
            return null;
        }
        return UsuarioEntity.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .senhaHash(usuario.getSenhaHash())
                .role(usuario.getRole())
                .criadoEm(usuario.getCriadoEm())
                .build();
    }

    public static Usuario toDomain(UsuarioEntity entity) {
        if (entity == null) {
            return null;
        }
        return Usuario.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .email(entity.getEmail())
                .senhaHash(entity.getSenhaHash())
                .role(entity.getRole())
                .criadoEm(entity.getCriadoEm())
                .build();
    }
}
