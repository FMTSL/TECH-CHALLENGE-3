package com.agendamento.application;

import com.agendamento.domain.Avaliacao;
import com.agendamento.infrastructure.AvaliacaoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AvaliacaoService {
    private final AvaliacaoRepository repo;

    public AvaliacaoService(AvaliacaoRepository repo) {
        this.repo = repo;
    }

    public Avaliacao salvar(Avaliacao a) {
        return repo.save(a);
    }

    public List<Avaliacao> listarPorProfissional(Long profissionalId) {
        return repo.findByProfissionalId(profissionalId);
    }

    public List<Avaliacao> listarPorEstabelecimento(Long estabelecimentoId) {
        return repo.findByEstabelecimentoId(estabelecimentoId);
    }
}
