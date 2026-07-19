package br.com.fiap.agendamento.application.dto;

import br.com.fiap.agendamento.domain.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegistrarUsuarioRequest(
        @NotBlank String nome,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 6, message = "senha deve ter no minimo 6 caracteres") String senha,
        @NotNull Role role
) {}
