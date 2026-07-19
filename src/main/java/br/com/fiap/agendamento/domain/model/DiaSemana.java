package br.com.fiap.agendamento.domain.model;

/** Dias da semana em portugues, usados nas janelas recorrentes de {@link HorarioDisponivel}. */
public enum DiaSemana {
    SEGUNDA, TERCA, QUARTA, QUINTA, SEXTA, SABADO, DOMINGO;

    public static DiaSemana fromJavaDayOfWeek(java.time.DayOfWeek dow) {
        return switch (dow) {
            case MONDAY -> SEGUNDA;
            case TUESDAY -> TERCA;
            case WEDNESDAY -> QUARTA;
            case THURSDAY -> QUINTA;
            case FRIDAY -> SEXTA;
            case SATURDAY -> SABADO;
            case SUNDAY -> DOMINGO;
        };
    }
}
