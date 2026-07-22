package br.com.fiap.agendamento.infrastructure.persistence.entity;

import br.com.fiap.agendamento.domain.model.DiaSemana;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;
import java.util.UUID;

/**
 * Representacao JPA de {@link br.com.fiap.agendamento.domain.model.HorarioDisponivel}, isolada
 * na camada de infraestrutura. A conversao entre esta entidade e o modelo de dominio e feita
 * por {@link br.com.fiap.agendamento.infrastructure.persistence.mapper.HorarioDisponivelMapper}.
 */
@Entity
@Table(name = "horarios_disponiveis")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HorarioDisponivelEntity {

    @Id
    private UUID id;

    @Column(name = "profissional_id", nullable = false)
    private UUID profissionalId;

    @Enumerated(EnumType.STRING)
    @Column(name = "dia_semana", nullable = false)
    private DiaSemana diaSemana;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fim", nullable = false)
    private LocalTime horaFim;
}
