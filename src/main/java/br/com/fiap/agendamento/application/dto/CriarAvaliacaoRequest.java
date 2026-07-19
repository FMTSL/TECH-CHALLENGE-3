package br.com.fiap.agendamento.application.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CriarAvaliacaoRequest(
        @NotNull UUID agendamentoId,
        @NotNull @Min(1) @Max(5) Integer nota,
        String comentario
) {}
