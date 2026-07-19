package br.com.fiap.agendamento.application.dto;

import br.com.fiap.agendamento.domain.model.Avaliacao;

import java.util.UUID;

public record AvaliacaoResponse(
        UUID id, UUID agendamentoId, UUID estabelecimentoId, UUID profissionalId,
        Integer nota, String comentario
) {
    public static AvaliacaoResponse from(Avaliacao a) {
        return new AvaliacaoResponse(
                a.getId(), a.getAgendamentoId(), a.getEstabelecimentoId(),
                a.getProfissionalId(), a.getNota(), a.getComentario()
        );
    }
}
