package br.com.fiap.agendamento.infrastructure.web.controller;

import br.com.fiap.agendamento.application.dto.EstabelecimentoRequest;
import br.com.fiap.agendamento.application.dto.EstabelecimentoResponse;
import br.com.fiap.agendamento.application.dto.FiltroBuscaEstabelecimento;
import br.com.fiap.agendamento.application.usecase.estabelecimento.BuscarEstabelecimentosUseCase;
import br.com.fiap.agendamento.application.usecase.estabelecimento.CadastrarEstabelecimentoUseCase;
import br.com.fiap.agendamento.infrastructure.web.AutenticacaoUtils;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/** Endpoints REST de cadastro e busca/filtragem avancada de estabelecimentos (features 1 e 5). */
@RestController
@RequestMapping("/api/estabelecimentos")
@RequiredArgsConstructor
@Tag(name = "Estabelecimentos", description = "Cadastro, busca e filtragem avancada de estabelecimentos")
public class EstabelecimentoController {

    private final CadastrarEstabelecimentoUseCase cadastrarEstabelecimentoUseCase;
    private final BuscarEstabelecimentosUseCase buscarEstabelecimentosUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EstabelecimentoResponse cadastrar(@RequestBody @Valid EstabelecimentoRequest request) {
        var dono = AutenticacaoUtils.usuarioAutenticadoId();
        return EstabelecimentoResponse.from(cadastrarEstabelecimentoUseCase.executar(request, dono), null);
    }

    @GetMapping
    public List<EstabelecimentoResponse> buscar(
            @RequestParam(required = false) @Parameter(description = "Nome ou cidade") String q,
            @RequestParam(required = false) @Parameter(description = "Nome do servico oferecido") String servico,
            @RequestParam(required = false) BigDecimal precoMin,
            @RequestParam(required = false) BigDecimal precoMax,
            @RequestParam(required = false) @Parameter(description = "Nota media minima (1-5)") Double notaMinima,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @Parameter(description = "Filtra apenas estabelecimentos com algum profissional livre nesta data") LocalDate disponivelEm) {

        var filtro = new FiltroBuscaEstabelecimento(q, servico, precoMin, precoMax, notaMinima, disponivelEm);
        return buscarEstabelecimentosUseCase.executar(filtro).stream()
                .map(e -> EstabelecimentoResponse.from(e, buscarEstabelecimentosUseCase.notaMedia(e.getId())))
                .toList();
    }

    @GetMapping("/{id}")
    public EstabelecimentoResponse buscarPorId(@PathVariable UUID id) {
        var estabelecimento = buscarEstabelecimentosUseCase.buscarPorId(id);
        return EstabelecimentoResponse.from(estabelecimento, buscarEstabelecimentosUseCase.notaMedia(estabelecimento.getId()));
    }
}
