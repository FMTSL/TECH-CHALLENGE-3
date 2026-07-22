package br.com.fiap.agendamento.domain.model;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Servico oferecido por um {@link Estabelecimento}, com preco e duracao usados no calculo de agenda.
 * Entidade de dominio pura, sem anotacoes de persistencia.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Servico {

    @Builder.Default
    private UUID id = UUID.randomUUID();

    private String nome;
    private String descricao;
    private BigDecimal preco;
    private Integer duracaoMinutos;
    private UUID estabelecimentoId;
}
