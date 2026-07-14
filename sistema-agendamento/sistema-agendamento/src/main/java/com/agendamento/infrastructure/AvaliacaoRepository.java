package com.agendamento.infrastructure;

import com.agendamento.domain.Avaliacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {
    List<Avaliacao> findByProfissionalId(Long profissionalId);
    List<Avaliacao> findByEstabelecimentoId(Long estabelecimentoId);
}
