package br.com.fiap.agendamento.infrastructure.web.controller;

import br.com.fiap.agendamento.application.dto.ServicoRequest;
import br.com.fiap.agendamento.application.dto.ServicoResponse;
import br.com.fiap.agendamento.application.usecase.servico.CadastrarServicoUseCase;
import br.com.fiap.agendamento.application.usecase.servico.ListarServicosUseCase;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/** Endpoints REST de cadastro e listagem do catalogo de servicos de um estabelecimento. */
@RestController
@RequestMapping("/api/servicos")
@RequiredArgsConstructor
@Tag(name = "Servicos", description = "Catalogo de servicos oferecidos pelos estabelecimentos")
public class ServicoController {

    private final CadastrarServicoUseCase cadastrarServicoUseCase;
    private final ListarServicosUseCase listarServicosUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ServicoResponse cadastrar(@RequestBody @Valid ServicoRequest request) {
        return ServicoResponse.from(cadastrarServicoUseCase.executar(request));
    }

    @GetMapping
    public List<ServicoResponse> listar(@RequestParam(required = false) UUID estabelecimentoId) {
        return listarServicosUseCase.executar(estabelecimentoId).stream()
                .map(ServicoResponse::from)
                .toList();
    }
}
