package br.com.fiap.agendamento.infrastructure.persistence.adapter;

import br.com.fiap.agendamento.domain.model.Usuario;
import br.com.fiap.agendamento.domain.repository.UsuarioRepository;
import br.com.fiap.agendamento.infrastructure.persistence.mapper.UsuarioMapper;
import br.com.fiap.agendamento.infrastructure.persistence.springdata.UsuarioJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * Implementacao do port {@link UsuarioRepository} sobre Spring Data JPA.
 * Traduz entre o modelo de dominio puro ({@link Usuario}) e a entidade de persistencia
 * ({@code UsuarioEntity}) a cada chamada, via {@link UsuarioMapper}.
 */
@Component
@RequiredArgsConstructor
public class UsuarioRepositoryAdapter implements UsuarioRepository {

    private final UsuarioJpaRepository jpaRepository;

    @Override
    public Usuario save(Usuario usuario) {
        var salvo = jpaRepository.save(UsuarioMapper.toEntity(usuario));
        return UsuarioMapper.toDomain(salvo);
    }

    @Override
    public Optional<Usuario> findById(UUID id) {
        return jpaRepository.findById(id).map(UsuarioMapper::toDomain);
    }

    @Override
    public Optional<Usuario> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(UsuarioMapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }
}
