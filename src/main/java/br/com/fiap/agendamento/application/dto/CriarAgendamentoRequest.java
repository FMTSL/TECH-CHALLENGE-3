package br.com.fiap.agendamento.application.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record CriarAgendamentoRequest(
        @NotNull UUID profissionalId,
        @NotNull UUID servicoId,
        @NotNull @Future LocalDateTime dataHora
) {}
