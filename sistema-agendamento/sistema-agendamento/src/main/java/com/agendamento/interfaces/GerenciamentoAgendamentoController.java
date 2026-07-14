package com.agendamento.interfaces;

import com.agendamento.application.GerenciamentoAgendamentoService;
import com.agendamento.domain.Agendamento;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/gerenciamento/agendamentos")
public class GerenciamentoAgendamentoController {
    private final GerenciamentoAgendamentoService service;

    public GerenciamentoAgendamentoController(GerenciamentoAgendamentoService service) {
        this.service = service;
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<Agendamento> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(service.cancelar(id));
    }

    @PutMapping("/{id}/reagendar")
    public ResponseEntity<Agendamento> reagendar(@PathVariable Long id, @RequestParam LocalDateTime novaDataHora) {
        return ResponseEntity.ok(service.reagendar(id, novaDataHora));
    }

    @PutMapping("/{id}/nao-compareceu")
    public ResponseEntity<Agendamento> naoCompareceu(@PathVariable Long id) {
        return ResponseEntity.ok(service.marcarNaoComparecimento(id));
    }

    @GetMapping("/estabelecimento/{id}")
    public List<Agendamento> listarPorEstabelecimento(@PathVariable Long id) {
        return service.listarPorEstabelecimento(id);
    }

    @GetMapping("/profissional/{id}")
    public List<Agendamento> listarPorProfissional(@PathVariable Long id) {
        return service.listarPorProfissional(id);
    }
}
