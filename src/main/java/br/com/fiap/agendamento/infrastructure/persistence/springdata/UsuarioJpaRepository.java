package br.com.fiap.agendamento.infrastructure.persistence.springdata;

import br.com.fiap.agendamento.infrastructure.persistence.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/** Repositorio Spring Data para {@link UsuarioEntity}. Detalhe de infraestrutura, usado apenas pelo adapter. */
public interface UsuarioJpaRepository extends JpaRepository<UsuarioEntity, UUID> {
    Optional<UsuarioEntity> findByEmail(String email);
    boolean existsByEmail(String email);
}
