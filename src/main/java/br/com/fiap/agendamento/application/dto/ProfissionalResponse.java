package br.com.fiap.agendamento.application.dto;

import br.com.fiap.agendamento.domain.model.Profissional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Resposta publica do perfil de profissional. Deliberadamente NAO expoe
 * {@code emailContato}: esse dado e usado internamente para notificacoes
 * (ver {@link br.com.fiap.agendamento.application.port.NotificacaoService})
 * mas nao deve ser exibido em uma listagem publica sem autenticacao.
 */
public record ProfissionalResponse(
        UUID id, String nome, List<String> especialidades, BigDecimal tarifaBase, UUID estabelecimentoId
) {
    public static ProfissionalResponse from(Profissional p) {
        return new ProfissionalResponse(
                p.getId(), p.getNome(), p.getEspecialidades(), p.getTarifaBase(), p.getEstabelecimentoId());
    }
}
