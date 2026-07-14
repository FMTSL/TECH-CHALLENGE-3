package com.agendamento.application;

import com.agendamento.domain.Estabelecimento;
import com.agendamento.domain.Profissional;
import com.agendamento.infrastructure.EstabelecimentoRepository;
import com.agendamento.infrastructure.ProfissionalRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BuscaService {
    private final EstabelecimentoRepository estabelecimentoRepo;
    private final ProfissionalRepository profissionalRepo;

    public BuscaService(EstabelecimentoRepository estabelecimentoRepo, ProfissionalRepository profissionalRepo) {
        this.estabelecimentoRepo = estabelecimentoRepo;
        this.profissionalRepo = profissionalRepo;
    }

    // Busca em estabelecimentos
    public List<Estabelecimento> buscarEstabelecimentosPorNome(String nome) {
        return estabelecimentoRepo.findByNomeContainingIgnoreCase(nome);
    }

    public List<Estabelecimento> buscarEstabelecimentosPorEndereco(String endereco) {
        return estabelecimentoRepo.findByEnderecoContainingIgnoreCase(endereco);
    }

    public List<Estabelecimento> buscarEstabelecimentosPorServico(String servico) {
        return estabelecimentoRepo.findByServicosContainingIgnoreCase(servico);
    }

    // Busca em profissionais
    public List<Profissional> buscarProfissionaisPorEspecialidade(String especialidade) {
        return profissionalRepo.findByEspecialidadeContainingIgnoreCase(especialidade);
    }

    public List<Profissional> buscarProfissionaisPorDisponibilidade(String disponibilidade) {
        return profissionalRepo.findByDisponibilidadeContainingIgnoreCase(disponibilidade);
    }

    public List<Profissional> buscarProfissionaisPorFaixaDePreco(Double min, Double max) {
        return profissionalRepo.findByTarifaBetween(min, max);
    }
}
