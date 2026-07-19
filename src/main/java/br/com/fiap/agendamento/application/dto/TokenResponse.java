package br.com.fiap.agendamento.application.dto;

public record TokenResponse(String token, String tipo) {
    public static TokenResponse of(String token) {
        return new TokenResponse(token, "Bearer");
    }
}
