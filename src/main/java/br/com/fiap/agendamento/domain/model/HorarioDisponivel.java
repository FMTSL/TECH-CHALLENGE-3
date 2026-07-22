package br.com.fiap.agendamento.domain.model;

import lombok.*;

import java.time.LocalTime;
import java.util.UUID;

/**
 * Janela recorrente semanal de disponibilidade de um profissional
 * (feature 2 do desafio: "horarios disponiveis" no perfil do profissional).
 * Ex: profissional X, TERCA, das 09:00 as 18:00.
 * Entidade de dominio pura, sem anotacoes de persistencia.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HorarioDisponivel {

    @Builder.Default
    private UUID id = UUID.randomUUID();

    private UUID profissionalId;
    private DiaSemana diaSemana;
    private LocalTime horaInicio;
    private LocalTime horaFim;

    public boolean contemHorario(LocalTime hora) {
        return !hora.isBefore(horaInicio) && hora.isBefore(horaFim);
    }
}
