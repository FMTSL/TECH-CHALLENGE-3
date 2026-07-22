package br.com.fiap.agendamento.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Representacao JPA de {@link br.com.fiap.agendamento.domain.model.Servico}, isolada
 * na camada de infraestrutura. A conversao entre esta entidade e o modelo de dominio e feita
 * por {@link br.com.fiap.agendamento.infrastructure.persistence.mapper.ServicoMapper}.
 */
@Entity
@Table(name = "servicos")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServicoEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String nome;

    private String descricao;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    @Column(name = "duracao_minutos", nullable = false)
    private Integer duracaoMinutos;

    @Column(name = "estabelecimento_id", nullable = false)
    private UUID estabelecimentoId;
}
