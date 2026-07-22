package br.com.fiap.agendamento.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Representacao JPA de {@link br.com.fiap.agendamento.domain.model.Estabelecimento}, isolada
 * na camada de infraestrutura. A conversao entre esta entidade e o modelo de dominio e feita
 * por {@link br.com.fiap.agendamento.infrastructure.persistence.mapper.EstabelecimentoMapper}.
 */
@Entity
@Table(name = "estabelecimentos")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstabelecimentoEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String endereco;

    private String cidade;

    @Column(name = "horario_funcionamento")
    private String horarioFuncionamento;

    @ElementCollection
    @CollectionTable(name = "estabelecimento_fotos", joinColumns = @JoinColumn(name = "estabelecimento_id"))
    @Column(name = "url_foto")
    @Builder.Default
    private List<String> fotos = new ArrayList<>();

    @Column(name = "usuario_dono_id", nullable = false)
    private UUID usuarioDonoId;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;
}
