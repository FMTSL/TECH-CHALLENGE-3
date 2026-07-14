package com.agendamento.interfaces;

import com.agendamento.domain.Agendamento;
import com.agendamento.application.AgendamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/agendamentos")
@Tag(name = "Agendamentos", description = "Endpoints para gerenciamento de agendamentos")
public class AgendamentoController {

    private final AgendamentoService service;

    public AgendamentoController(AgendamentoService service) {
        this.service = service;
    }

    @Operation(summary = "Criar um novo agendamento", description = "Cria um agendamento para um cliente com um profissional em um estabelecimento.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Agendamento criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou horário ocupado")
    })
    @PostMapping
    public ResponseEntity<Agendamento> criar(@org.springframework.web.bind.annotation.RequestBody Agendamento agendamento) {
        Agendamento criado = service.criar(agendamento);
        return ResponseEntity.ok(criado);
    }

    @Operation(summary = "Listar todos os agendamentos", description = "Retorna a lista completa de agendamentos cadastrados.")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<Agendamento>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @Operation(summary = "Buscar agendamento por ID", description = "Retorna os detalhes de um agendamento específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Agendamento encontrado"),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Agendamento> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.of(service.buscarPorId(id));
    }

    @Operation(summary = "Cancelar um agendamento existente", description = "Altera o status do agendamento para CANCELADO.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Agendamento cancelado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Agendamento> cancelar(@PathVariable Long id) {
        Agendamento cancelado = service.cancelar(id);
        return ResponseEntity.ok(cancelado);
    }

    @Operation(summary = "Reagendar um agendamento existente", description = "Atualiza a data/hora de um agendamento e altera o status para REAGENDADO.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Agendamento reagendado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Horário já ocupado"),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado")
    })
    @PutMapping("/{id}/reagendar")
    public ResponseEntity<Agendamento> reagendar(@PathVariable Long id, @org.springframework.web.bind.annotation.RequestBody Agendamento novoAgendamento) {
        Agendamento reagendado = service.reagendar(id, novoAgendamento.getDataHora());
        return ResponseEntity.ok(reagendado);
    }
}
