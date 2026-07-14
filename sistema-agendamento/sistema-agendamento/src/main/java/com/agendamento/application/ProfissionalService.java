package com.agendamento.application;

import com.agendamento.domain.Profissional;
import com.agendamento.infrastructure.ProfissionalRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProfissionalService {
    private final ProfissionalRepository repo;

    public ProfissionalService(ProfissionalRepository repo) {
        this.repo = repo;
    }

    public Profissional salvar(Profissional p) {
        return repo.save(p);
    }

    public List<Profissional> listarTodos() {
        return repo.findAll();
    }

    public Optional<Profissional> buscarPorId(Long id) {
        return repo.findById(id);
    }

    public List<Profissional> buscarPorEspecialidade(String especialidade) {
        return repo.findByEspecialidadeContainingIgnoreCase(especialidade);
    }

    public List<Profissional> listarPorEstabelecimento(Long estabelecimentoId) {
        return repo.findByEstabelecimentoId(estabelecimentoId);
    }

    public List<Profissional> buscarPorDisponibilidade(String disponibilidade) {
        return repo.findByDisponibilidadeContainingIgnoreCase(disponibilidade);
    }

    public List<Profissional> buscarPorFaixaDeTarifa(Double min, Double max) {
        return repo.findByTarifaBetween(min, max);
    }
}