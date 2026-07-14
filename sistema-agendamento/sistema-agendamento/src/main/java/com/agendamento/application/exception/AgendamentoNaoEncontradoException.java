package com.agendamento.application.exception;

public class AgendamentoNaoEncontradoException extends RuntimeException {
    public AgendamentoNaoEncontradoException(String msg) {
        super(msg);
    }
}