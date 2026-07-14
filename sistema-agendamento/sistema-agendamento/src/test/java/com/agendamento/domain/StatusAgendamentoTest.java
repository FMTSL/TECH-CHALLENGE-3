package com.agendamento.domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StatusAgendamentoTest {

    @Test
    void deveConterTodosOsValores() {
        StatusAgendamento[] valores = StatusAgendamento.values();
        assertEquals(4, valores.length);
        assertTrue(List.of(valores).contains(StatusAgendamento.CONFIRMADO));
        assertTrue(List.of(valores).contains(StatusAgendamento.CANCELADO));
        assertTrue(List.of(valores).contains(StatusAgendamento.REAGENDADO));
        assertTrue(List.of(valores).contains(StatusAgendamento.NAO_COMPARECEU));
    }

    @Test
    void deveConverterStringParaEnum() {
        assertEquals(StatusAgendamento.CONFIRMADO, StatusAgendamento.valueOf("CONFIRMADO"));
        assertEquals(StatusAgendamento.CANCELADO, StatusAgendamento.valueOf("CANCELADO"));
    }
}
