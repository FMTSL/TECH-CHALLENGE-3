package br.com.fiap.agendamento.infrastructure.web.controller;

import br.com.fiap.agendamento.application.dto.AvaliacaoResponse;
import br.com.fiap.agendamento.application.dto.CriarAvaliacaoRequest;
import br.com.fiap.agendamento.application.usecase.avaliacao.CriarAvaliacaoUseCase;
import br.com.fiap.agendamento.infrastructure.web.AutenticacaoUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/** Endpoint REST para o cliente avaliar um atendimento concluido (feature 4). */
@RestController
@RequestMapping("/api/avaliacoes")
@RequiredArgsConstructor
@Tag(name = "Avaliacoes", description = "Avaliacoes e comentarios pos-atendimento")
public class AvaliacaoController {

    private final CriarAvaliacaoUseCase criarAvaliacaoUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AvaliacaoResponse criar(@RequestBody @Valid CriarAvaliacaoRequest request) {
        var clienteId = AutenticacaoUtils.usuarioAutenticadoId();
        return AvaliacaoResponse.from(criarAvaliacaoUseCase.executar(request, clienteId));
    }
}
