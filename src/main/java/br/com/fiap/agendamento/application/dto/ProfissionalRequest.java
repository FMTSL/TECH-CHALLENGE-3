package br.com.fiap.agendamento.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ProfissionalRequest(
        @NotBlank String nome,
        List<String> especialidades,
        BigDecimal tarifaBase,
        @NotNull UUID estabelecimentoId,
        @Email String emailContato
) {}
