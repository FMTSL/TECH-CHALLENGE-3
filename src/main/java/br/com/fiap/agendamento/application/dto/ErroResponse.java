package br.com.fiap.agendamento.application.dto;

import java.time.Instant;

public record ErroResponse(Instant timestamp, int status, String erro, String mensagem, String caminho) {}
