package br.com.fiap.agendamento.application.dto;

import br.com.fiap.agendamento.domain.model.Agendamento;
import br.com.fiap.agendamento.domain.model.StatusAgendamento;

import java.time.LocalDateTime;
import java.util.UUID;

public record AgendamentoResponse(
        UUID id, UUID clienteId, UUID profissionalId, UUID servicoId,
        UUID estabelecimentoId, LocalDateTime dataHora, StatusAgendamento status
) {
    public static AgendamentoResponse from(Agendamento a) {
        return new AgendamentoResponse(
                a.getId(), a.getClienteId(), a.getProfissionalId(), a.getServicoId(),
                a.getEstabelecimentoId(), a.getDataHora(), a.getStatus()
        );
    }
}
