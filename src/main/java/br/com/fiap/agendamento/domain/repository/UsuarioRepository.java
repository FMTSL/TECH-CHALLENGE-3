package br.com.fiap.agendamento.domain.repository;

import br.com.fiap.agendamento.domain.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Port de persistencia para Usuario. Estende JpaRepository (trade-off pragmatico:
 * evita uma camada extra de adapter/mapper para um CRUD simples, mantendo o
 * Spring Data como detalhe de infraestrutura injetado nos use cases via interface).
 */
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);
}
