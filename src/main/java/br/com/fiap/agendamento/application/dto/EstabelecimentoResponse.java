package br.com.fiap.agendamento.application.dto;

import br.com.fiap.agendamento.domain.model.Estabelecimento;

import java.util.List;
import java.util.UUID;

public record EstabelecimentoResponse(
        UUID id, String nome, String endereco, String cidade,
        String horarioFuncionamento, List<String> fotos, Double notaMedia
) {
    public static EstabelecimentoResponse from(Estabelecimento e, Double notaMedia) {
        return new EstabelecimentoResponse(
                e.getId(), e.getNome(), e.getEndereco(), e.getCidade(),
                e.getHorarioFuncionamento(), e.getFotos(), notaMedia
        );
    }
}
