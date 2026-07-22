package br.com.fiap.agendamento.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Representacao JPA de {@link br.com.fiap.agendamento.domain.model.Avaliacao}, isolada
 * na camada de infraestrutura. A conversao entre esta entidade e o modelo de dominio e feita
 * por {@link br.com.fiap.agendamento.infrastructure.persistence.mapper.AvaliacaoMapper}.
 */
@Entity
@Table(name = "avaliacoes")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvaliacaoEntity {

    @Id
    private UUID id;

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

    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;
}
