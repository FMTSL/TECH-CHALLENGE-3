package br.com.fiap.agendamento.domain.repository;

import br.com.fiap.agendamento.domain.model.Usuario;

import java.util.Optional;
import java.util.UUID;

/**
 * Port de persistencia para {@link Usuario}. Interface pura de dominio: nao conhece JPA,
 * Spring Data ou qualquer outro detalhe de infraestrutura. A implementacao concreta
 * (adapter sobre Spring Data JPA) vive em infrastructure.persistence.adapter.
 */
public interface UsuarioRepository {
    Usuario save(Usuario usuario);
    Optional<Usuario> findById(UUID id);
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);
}
