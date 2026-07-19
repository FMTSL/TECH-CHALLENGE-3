package br.com.fiap.agendamento.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** Estabelecimento de beleza/bem-estar cadastrado por um {@link Usuario} dono (feature 1 do desafio). */
@Entity
@Table(name = "estabelecimentos")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Estabelecimento {

    @Id
    @Builder.Default
    private UUID id = UUID.randomUUID();

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String endereco;

    private String cidade;

    @Column(name = "horario_funcionamento")
    private String horarioFuncionamento;

    @ElementCollection
    @CollectionTable(name = "estabelecimento_fotos", joinColumns = @JoinColumn(name = "estabelecimento_id"))
    @Column(name = "url_foto")
    @Builder.Default
    private List<String> fotos = new ArrayList<>();

    @Column(name = "usuario_dono_id", nullable = false)
    private UUID usuarioDonoId;

    @Builder.Default
    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm = Instant.now();
}
