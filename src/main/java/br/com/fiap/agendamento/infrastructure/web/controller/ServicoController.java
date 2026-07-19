package br.com.fiap.agendamento.infrastructure.web.controller;

import br.com.fiap.agendamento.application.dto.ServicoRequest;
import br.com.fiap.agendamento.application.usecase.servico.CadastrarServicoUseCase;
import br.com.fiap.agendamento.application.usecase.servico.ListarServicosUseCase;
import br.com.fiap.agendamento.domain.model.Servico;
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
    public Servico cadastrar(@RequestBody @Valid ServicoRequest request) {
        return cadastrarServicoUseCase.executar(request);
    }

    @GetMapping
    public List<Servico> listar(@RequestParam(required = false) UUID estabelecimentoId) {
        return listarServicosUseCase.executar(estabelecimentoId);
    }
}
