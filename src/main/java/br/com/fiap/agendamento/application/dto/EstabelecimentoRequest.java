package br.com.fiap.agendamento.application.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record EstabelecimentoRequest(
        @NotBlank String nome,
        @NotBlank String endereco,
        String cidade,
        String horarioFuncionamento,
        List<String> fotos
) {}
