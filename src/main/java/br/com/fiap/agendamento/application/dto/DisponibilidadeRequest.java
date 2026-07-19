package br.com.fiap.agendamento.application.dto;

import br.com.fiap.agendamento.domain.model.DiaSemana;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public record DisponibilidadeRequest(
        @NotNull DiaSemana diaSemana,
        @NotNull LocalTime horaInicio,
        @NotNull LocalTime horaFim
) {}
