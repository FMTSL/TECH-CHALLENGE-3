package br.com.fiap.agendamento.domain.model;

import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Estabelecimento de beleza/bem-estar cadastrado por um {@link Usuario} dono (feature 1 do desafio).
 * Entidade de dominio pura, sem anotacoes de persistencia.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Estabelecimento {

    @Builder.Default
    private UUID id = UUID.randomUUID();

    private String nome;
    private String endereco;
    private String cidade;
    private String horarioFuncionamento;

    @Builder.Default
    private List<String> fotos = new ArrayList<>();

    private UUID usuarioDonoId;

    @Builder.Default
    private Instant criadoEm = Instant.now();
}
