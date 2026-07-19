package br.com.fiap.agendamento.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

/** Avaliacao (nota + comentario) deixada pelo cliente apos um {@link Agendamento} concluido. */
@Entity
@Table(name = "avaliacoes")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Avaliacao {

    @Id
    @Builder.Default
    private UUID id = UUID.randomUUID();

    @Column(name = "agendamento_id", nullable = false, unique = true)
    private UUID agendamentoId;

    @Column(name = "cliente_id", nullable = false)
    private UUID clienteId;

    @Column(name = "estabelecimento_id", nullable = false)
    private UUID estabelecimentoId;

    @Column(name = "profissional_id", nullable = false)
    private UUID profissionalId;

    @Column(nullable = false)
    private Integer nota;

    private String comentario;

    @Builder.Default
    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm = Instant.now();
}
