package br.com.fiap.agendamento.application.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ReagendarRequest(@NotNull @Future LocalDateTime novaDataHora) {}
