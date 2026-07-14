package com.agendamento.infrastructure;

import com.agendamento.domain.Estabelecimento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EstabelecimentoRepository extends JpaRepository<Estabelecimento, Long> {
    List<Estabelecimento> findByNomeContainingIgnoreCase(String nome);
    List<Estabelecimento> findByEnderecoContainingIgnoreCase(String endereco);
    List<Estabelecimento> findByServicosContainingIgnoreCase(String servico);
}