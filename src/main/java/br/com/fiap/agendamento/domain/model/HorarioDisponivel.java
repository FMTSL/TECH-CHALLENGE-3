package br.com.fiap.agendamento.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;
import java.util.UUID;

/**
 * Janela recorrente semanal de disponibilidade de um profissional
 * (feature 2 do desafio: "horarios disponiveis" no perfil do profissional).
 * Ex: profissional X, TERCA, das 09:00 as 18:00.
 */
@Entity
@Table(name = "horarios_disponiveis")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HorarioDisponivel {

    @Id
    @Builder.Default
    private UUID id = UUID.randomUUID();

    @Column(name = "profissional_id", nullable = false)
    private UUID profissionalId;

    @Enumerated(EnumType.STRING)
    @Column(name = "dia_semana", nullable = false)
    private DiaSemana diaSemana;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fim", nullable = false)
    private LocalTime horaFim;

    public boolean contemHorario(LocalTime hora) {
        return !hora.isBefore(horaInicio) && hora.isBefore(horaFim);
    }
}
