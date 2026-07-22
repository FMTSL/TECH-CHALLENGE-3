package br.com.fiap.agendamento.domain.repository;

import br.com.fiap.agendamento.domain.model.Estabelecimento;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Port de persistencia para {@link Estabelecimento}, com busca textual por nome/cidade.
 * Interface pura de dominio, sem dependencia de JPA/Spring Data.
 */
public interface EstabelecimentoRepository {
    Estabelecimento save(Estabelecimento estabelecimento);
    Optional<Estabelecimento> findById(UUID id);
    boolean existsById(UUID id);
    List<Estabelecimento> findAll();
    List<Estabelecimento> findByNomeContainingIgnoreCaseOrCidadeContainingIgnoreCase(String nome, String cidade);
    List<Estabelecimento> findByCidadeIgnoreCase(String cidade);
}
