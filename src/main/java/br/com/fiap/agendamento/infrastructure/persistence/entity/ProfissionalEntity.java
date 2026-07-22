package br.com.fiap.agendamento.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Representacao JPA de {@link br.com.fiap.agendamento.domain.model.Profissional}, isolada
 * na camada de infraestrutura. A conversao entre esta entidade e o modelo de dominio e feita
 * por {@link br.com.fiap.agendamento.infrastructure.persistence.mapper.ProfissionalMapper}.
 */
@Entity
@Table(name = "profissionais")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfissionalEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String nome;

    @ElementCollection
    @CollectionTable(name = "profissional_especialidades", joinColumns = @JoinColumn(name = "profissional_id"))
    @Column(name = "especialidade")
    @Builder.Default
    private List<String> especialidades = new ArrayList<>();

    @Column(name = "tarifa_base", precision = 10, scale = 2)
    private BigDecimal tarifaBase;

    @Column(name = "estabelecimento_id", nullable = false)
    private UUID estabelecimentoId;

    @Column(name = "email_contato")
    private String emailContato;
}
