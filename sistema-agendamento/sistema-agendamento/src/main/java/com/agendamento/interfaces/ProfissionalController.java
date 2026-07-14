package com.agendamento.interfaces;

import com.agendamento.domain.Profissional;
import com.agendamento.application.ProfissionalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/profissionais")
@Tag(name = "Profissionais", description = "Endpoints para cadastro e consulta de profissionais")
public class ProfissionalController {
    private final ProfissionalService service;

    public ProfissionalController(ProfissionalService service) {
        this.service = service;
    }

    @Operation(summary = "Cadastrar um novo profissional", description = "Cadastra um novo profissional vinculado a um estabelecimento.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profissional cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping
    public ResponseEntity<Profissional> cadastrar(@RequestBody Profissional p) {
        return ResponseEntity.ok(service.salvar(p));
    }

    @Operation(summary = "Listar todos os profissionais", description = "Retorna a lista completa de profissionais cadastrados.")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<Profissional>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @Operation(summary = "Buscar profissional por ID", description = "Retorna os detalhes de um profissional específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profissional encontrado"),
            @ApiResponse(responseCode = "404", description = "Profissional não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Profissional> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.of(service.buscarPorId(id));
    }

    @Operation(summary = "Buscar profissionais por especialidade", description = "Retorna todos os profissionais que possuem a especialidade informada.")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping("/especialidade/{especialidade}")
    public ResponseEntity<List<Profissional>> buscarPorEspecialidade(@PathVariable String especialidade) {
        return ResponseEntity.ok(service.buscarPorEspecialidade(especialidade));
    }

    @Operation(summary = "Listar profissionais por estabelecimento", description = "Retorna todos os profissionais vinculados a um determinado estabelecimento.")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping("/estabelecimento/{id}")
    public ResponseEntity<List<Profissional>> listarPorEstabelecimento(@PathVariable Long id) {
        return ResponseEntity.ok(service.listarPorEstabelecimento(id));
    }

    @Operation(summary = "Buscar profissionais por disponibilidade", description = "Retorna todos os profissionais que possuem a disponibilidade informada.")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping("/disponibilidade/{disponibilidade}")
    public ResponseEntity<List<Profissional>> buscarPorDisponibilidade(@PathVariable String disponibilidade) {
        return ResponseEntity.ok(service.buscarPorDisponibilidade(disponibilidade));
    }

    @Operation(summary = "Buscar profissionais por faixa de tarifa", description = "Retorna todos os profissionais que possuem tarifa dentro da faixa informada.")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping("/tarifa")
    public ResponseEntity<List<Profissional>> buscarPorFaixaDeTarifa(@RequestParam Double min, @RequestParam Double max) {
        return ResponseEntity.ok(service.buscarPorFaixaDeTarifa(min, max));
    }
}
