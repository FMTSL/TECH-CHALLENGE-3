package br.com.fiap.agendamento.domain.model;

/** Ciclo de vida de um agendamento. */
public enum StatusAgendamento {
    PENDENTE,
    CONFIRMADO,
    CANCELADO,
    CONCLUIDO,
    NAO_COMPARECEU
}
