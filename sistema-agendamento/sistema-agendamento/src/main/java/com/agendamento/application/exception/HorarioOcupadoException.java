package com.agendamento.application.exception;

public class HorarioOcupadoException extends RuntimeException {
    public HorarioOcupadoException(String msg) {
        super(msg);
    }
}