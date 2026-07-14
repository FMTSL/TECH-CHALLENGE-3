package com.agendamento.application;

import com.agendamento.domain.Estabelecimento;
import com.agendamento.infrastructure.EstabelecimentoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EstabelecimentoService {
    private final EstabelecimentoRepository repo;

    public EstabelecimentoService(EstabelecimentoRepository repo) {
        this.repo = repo;
    }

    public Estabelecimento salvar(Estabelecimento e) {
        return repo.save(e);
    }

    public List<Estabelecimento> listarTodos() {
        return repo.findAll();
    }

    public Optional<Estabelecimento> buscarPorId(Long id) {
        return repo.findById(id);
    }
}
