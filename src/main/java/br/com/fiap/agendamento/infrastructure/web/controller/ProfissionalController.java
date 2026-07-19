package br.com.fiap.agendamento.infrastructure.web.controller;

import br.com.fiap.agendamento.application.dto.ProfissionalRequest;
import br.com.fiap.agendamento.application.dto.ProfissionalResponse;
import br.com.fiap.agendamento.application.usecase.profissional.CadastrarProfissionalUseCase;
import br.com.fiap.agendamento.application.usecase.profissional.ListarProfissionaisUseCase;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/** Endpoints REST de cadastro e listagem de profissionais (feature 2). */
@RestController
@RequestMapping("/api/profissionais")
@RequiredArgsConstructor
@Tag(name = "Profissionais", description = "Perfis de profissionais e suas especialidades")
public class ProfissionalController {

    private final CadastrarProfissionalUseCase cadastrarProfissionalUseCase;
    private final ListarProfissionaisUseCase listarProfissionaisUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProfissionalResponse cadastrar(@RequestBody @Valid ProfissionalRequest request) {
        return ProfissionalResponse.from(cadastrarProfissionalUseCase.executar(request));
    }

    @GetMapping
    public List<ProfissionalResponse> listar(@RequestParam(required = false) UUID estabelecimentoId) {
        return listarProfissionaisUseCase.executar(estabelecimentoId).stream()
                .map(ProfissionalResponse::from)
                .toList();
    }
}
