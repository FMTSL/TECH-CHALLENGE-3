package com.agendamento.interfaces;

import com.agendamento.domain.Avaliacao;
import com.agendamento.application.AvaliacaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/avaliacoes")
public class AvaliacaoController {
    private final AvaliacaoService service;

    public AvaliacaoController(AvaliacaoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Avaliacao> cadastrar(@RequestBody Avaliacao a) {
        return ResponseEntity.ok(service.salvar(a));
    }

    @GetMapping("/profissional/{id}")
    public List<Avaliacao> listarPorProfissional(@PathVariable Long id) {
        return service.listarPorProfissional(id);
    }

    @GetMapping("/estabelecimento/{id}")
    public List<Avaliacao> listarPorEstabelecimento(@PathVariable Long id) {
        return service.listarPorEstabelecimento(id);
    }
}
