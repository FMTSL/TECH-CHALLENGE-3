package br.com.fiap.agendamento.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Perfil de um profissional vinculado a um {@link Estabelecimento} (feature 2 do desafio):
 * especialidades, tarifa base e um e-mail de contato opcional usado para notificacoes
 * (confirmacao/lembrete/cancelamento) e para o feed de calendario individual do profissional.
 *
 * <p>O profissional nao possui conta de login no sistema (quem se autentica e o
 * {@link Usuario} dono do estabelecimento); o e-mail de contato existe apenas para
 * que ele tambem receba as notificacoes exigidas pelo enunciado, sem a complexidade
 * de um segundo fluxo de autenticacao.</p>
 */
@Entity
@Table(name = "profissionais")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Profissional {

    @Id
    @Builder.Default
    private UUID id = UUID.randomUUID();

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

    /** Opcional: usado para notificacoes e feed de calendario do profissional. */
    @Column(name = "email_contato")
    private String emailContato;
}
