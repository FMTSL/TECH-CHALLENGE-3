package br.com.fiap.agendamento.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

/**
 * Representa um usuario autenticavel do sistema (cliente ou dono de estabelecimento).
 * Entidade de dominio pura: nao possui nenhuma anotacao ou dependencia de framework
 * (JPA, Spring, etc). A persistencia e resolvida inteiramente em infrastructure.persistence,
 * atraves de um mapper e uma entidade JPA equivalente (ver UsuarioEntity/UsuarioMapper).
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Builder.Default
    private UUID id = UUID.randomUUID();

    private String nome;
    private String email;
    private String senhaHash;
    private Role role;

    @Builder.Default
    private Instant criadoEm = Instant.now();
}
