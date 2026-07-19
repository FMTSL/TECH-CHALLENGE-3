package br.com.fiap.agendamento.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Nucleo de negocio do sistema. A unique constraint (profissional_id, data_hora)
 * definida na migration V1 impede double-booking mesmo sob concorrencia -
 * a validacao em memoria no use case cobre o caso feliz, e o banco garante a
 * integridade no caso de corrida (duas requisicoes simultaneas).
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
public class Agendamento {

    @Id
    @Builder.Default
    private UUID id = UUID.randomUUID();

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
    @Builder.Default
    @Column(nullable = false)
    private StatusAgendamento status = StatusAgendamento.PENDENTE;

    @Builder.Default
    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm = Instant.now();

    /** Controla se o lembrete automatico (24h antes) ja foi disparado, evitando duplicidade. */
    @Builder.Default
    @Column(name = "lembrete_enviado", nullable = false)
    private boolean lembreteEnviado = false;

    /** Regra de negocio: so pode cancelar se ainda nao ocorreu e nao esta ja cancelado/concluido. */
    public boolean podeCancelar() {
        return status == StatusAgendamento.PENDENTE || status == StatusAgendamento.CONFIRMADO;
    }
}
