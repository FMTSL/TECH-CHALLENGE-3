package br.com.fiap.agendamento.application.dto;

import br.com.fiap.agendamento.domain.model.Servico;

import java.math.BigDecimal;
import java.util.UUID;

/** Resposta publica de um servico do catalogo de um estabelecimento. */
public record ServicoResponse(
        UUID id, String nome, String descricao, BigDecimal preco, Integer duracaoMinutos, UUID estabelecimentoId
) {
    public static ServicoResponse from(Servico s) {
        return new ServicoResponse(
                s.getId(), s.getNome(), s.getDescricao(), s.getPreco(), s.getDuracaoMinutos(), s.getEstabelecimentoId());
    }
}
