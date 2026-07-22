package br.com.fiap.agendamento.domain.model;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Avaliacao (nota + comentario) deixada pelo cliente apos um {@link Agendamento} concluido.
 * Entidade de dominio pura, sem anotacoes de persistencia.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Avaliacao {

    @Builder.Default
    private UUID id = UUID.randomUUID();

    private UUID agendamentoId;
    private UUID clienteId;
    private UUID estabelecimentoId;
    private UUID profissionalId;
    private Integer nota;
    private String comentario;

    @Builder.Default
    private Instant criadoEm = Instant.now();
}
