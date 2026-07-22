package br.com.fiap.agendamento.domain.model;

import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Nucleo de negocio do sistema. Entidade de dominio pura, sem anotacoes de persistencia -
 * a unicidade (profissional_id, data_hora) que impede double-booking e garantida por uma
 * constraint definida na migration V1 e mapeada em {@code AgendamentoEntity}, na camada de
 * infraestrutura; a validacao em memoria no use case cobre o caso feliz, e o banco garante a
 * integridade no caso de corrida (duas requisicoes simultaneas).
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Agendamento {

    @Builder.Default
    private UUID id = UUID.randomUUID();

    private UUID clienteId;
    private UUID profissionalId;
    private UUID servicoId;
    private UUID estabelecimentoId;
    private LocalDateTime dataHora;

    @Builder.Default
    private StatusAgendamento status = StatusAgendamento.PENDENTE;

    @Builder.Default
    private Instant criadoEm = Instant.now();

    /** Controla se o lembrete automatico (24h antes) ja foi disparado, evitando duplicidade. */
    @Builder.Default
    private boolean lembreteEnviado = false;

    /** Regra de negocio: so pode cancelar se ainda nao ocorreu e nao esta ja cancelado/concluido. */
    public boolean podeCancelar() {
        return status == StatusAgendamento.PENDENTE || status == StatusAgendamento.CONFIRMADO;
    }
}
