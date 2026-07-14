package com.agendamento.interfaces;

import com.agendamento.domain.Estabelecimento;
import com.agendamento.domain.Profissional;
import com.agendamento.application.BuscaService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/busca")
public class BuscaController {
    private final BuscaService service;

    public BuscaController(BuscaService service) {
        this.service = service;
    }

    @GetMapping("/estabelecimentos/nome/{nome}")
    public List<Estabelecimento> buscarEstabelecimentosPorNome(@PathVariable String nome) {
        return service.buscarEstabelecimentosPorNome(nome);
    }

    @GetMapping("/estabelecimentos/endereco/{endereco}")
    public List<Estabelecimento> buscarEstabelecimentosPorEndereco(@PathVariable String endereco) {
        return service.buscarEstabelecimentosPorEndereco(endereco);
    }

    @GetMapping("/estabelecimentos/servico/{servico}")
    public List<Estabelecimento> buscarEstabelecimentosPorServico(@PathVariable String servico) {
        return service.buscarEstabelecimentosPorServico(servico);
    }

    @GetMapping("/profissionais/especialidade/{especialidade}")
    public List<Profissional> buscarProfissionaisPorEspecialidade(@PathVariable String especialidade) {
        return service.buscarProfissionaisPorEspecialidade(especialidade);
    }

    @GetMapping("/profissionais/disponibilidade/{disponibilidade}")
    public List<Profissional> buscarProfissionaisPorDisponibilidade(@PathVariable String disponibilidade) {
        return service.buscarProfissionaisPorDisponibilidade(disponibilidade);
    }

    @GetMapping("/profissionais/preco")
    public List<Profissional> buscarProfissionaisPorFaixaDePreco(@RequestParam Double min, @RequestParam Double max) {
        return service.buscarProfissionaisPorFaixaDePreco(min, max);
    }
}
