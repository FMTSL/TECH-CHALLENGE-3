package br.com.fiap.agendamento.application.dto;

import br.com.fiap.agendamento.domain.model.DiaSemana;
import br.com.fiap.agendamento.domain.model.HorarioDisponivel;

import java.time.LocalTime;
import java.util.UUID;

public record DisponibilidadeResponse(UUID id, DiaSemana diaSemana, LocalTime horaInicio, LocalTime horaFim) {
    public static DisponibilidadeResponse from(HorarioDisponivel h) {
        return new DisponibilidadeResponse(h.getId(), h.getDiaSemana(), h.getHoraInicio(), h.getHoraFim());
    }
}
