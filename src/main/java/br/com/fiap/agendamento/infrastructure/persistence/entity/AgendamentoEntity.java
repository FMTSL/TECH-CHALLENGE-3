package br.com.fiap.agendamento.infrastructure.persistence.entity;

import br.com.fiap.agendamento.domain.model.StatusAgendamento;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Representacao JPA de {@link br.com.fiap.agendamento.domain.model.Agendamento}, isolada na
 * camada de infraestrutura. A constraint unica (profissional_id, data_hora) - que impede
 * double-booking mesmo sob concorrencia real - vive aqui, junto do restante do mapeamento
 * de persistencia, e nao no modelo de dominio. A conversao entre esta entidade e o modelo
 * de dominio e feita por {@link br.com.fiap.agendamento.infrastructure.persistence.mapper.AgendamentoMapper}.
 */
@Entity
@Table(
    name = "agendamentos",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_profissional_data_hora",
        columnNames = {"profissional_id", "data_hora"}
    )
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgendamentoEntity {

    @Id
    private UUID id;

    @Column(name = "cliente_id", nullable = false)
    private UUID clienteId;

    @Column(name = "profissional_id", nullable = false)
    private UUID profissionalId;

    @Column(name = "servico_id", nullable = false)
    private UUID servicoId;

    @Column(name = "estabelecimento_id", nullable = false)
    private UUID estabelecimentoId;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusAgendamento status;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    @Column(name = "lembrete_enviado", nullable = false)
    private boolean lembreteEnviado;
}
