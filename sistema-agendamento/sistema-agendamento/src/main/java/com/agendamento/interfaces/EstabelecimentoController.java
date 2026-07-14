package com.agendamento.interfaces;

import com.agendamento.domain.Estabelecimento;
import com.agendamento.application.EstabelecimentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/estabelecimentos")
@Tag(name = "Estabelecimentos", description = "Endpoints para cadastro e consulta de estabelecimentos")
public class EstabelecimentoController {

    private final EstabelecimentoService service;

    public EstabelecimentoController(EstabelecimentoService service) {
        this.service = service;
    }

    @Operation(summary = "Criar um novo estabelecimento", description = "Cadastra um novo estabelecimento de beleza ou bem-estar.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estabelecimento criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping
    public ResponseEntity<Estabelecimento> cadastrar(@RequestBody Estabelecimento e) {
        return ResponseEntity.ok(service.salvar(e));
    }

    @Operation(summary = "Listar todos os estabelecimentos", description = "Retorna a lista completa de estabelecimentos cadastrados.")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<Estabelecimento>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @Operation(summary = "Buscar estabelecimento por ID", description = "Retorna os detalhes de um estabelecimento específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estabelecimento encontrado"),
            @ApiResponse(responseCode = "404", description = "Estabelecimento não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Estabelecimento> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.of(service.buscarPorId(id));
    }
}
