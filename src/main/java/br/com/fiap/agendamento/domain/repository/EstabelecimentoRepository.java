package br.com.fiap.agendamento.domain.repository;

import br.com.fiap.agendamento.domain.model.Estabelecimento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/** Port de persistencia para {@link br.com.fiap.agendamento.domain.model.Estabelecimento}, com busca textual por nome/cidade. */
public interface EstabelecimentoRepository extends JpaRepository<Estabelecimento, UUID> {
    List<Estabelecimento> findByNomeContainingIgnoreCaseOrCidadeContainingIgnoreCase(String nome, String cidade);
    List<Estabelecimento> findByCidadeIgnoreCase(String cidade);
}
