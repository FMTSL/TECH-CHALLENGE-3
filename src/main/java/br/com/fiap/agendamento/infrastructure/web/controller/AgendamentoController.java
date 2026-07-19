package br.com.fiap.agendamento.infrastructure.web.controller;

import br.com.fiap.agendamento.application.dto.AgendamentoResponse;
import br.com.fiap.agendamento.application.dto.CriarAgendamentoRequest;
import br.com.fiap.agendamento.application.dto.ReagendarRequest;
import br.com.fiap.agendamento.application.usecase.agendamento.AtualizarStatusAgendamentoUseCase;
import br.com.fiap.agendamento.application.usecase.agendamento.CancelarAgendamentoUseCase;
import br.com.fiap.agendamento.application.usecase.agendamento.CriarAgendamentoUseCase;
import br.com.fiap.agendamento.application.usecase.agendamento.ListarAgendamentosUseCase;
import br.com.fiap.agendamento.application.usecase.agendamento.ReagendarAgendamentoUseCase;
import br.com.fiap.agendamento.domain.model.StatusAgendamento;
import br.com.fiap.agendamento.infrastructure.web.AutenticacaoUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/** Endpoints REST do nucleo de negocio: criar, listar, cancelar, reagendar e mudar status de agendamentos (features 3 e 6). */
@RestController
@RequestMapping("/api/agendamentos")
@RequiredArgsConstructor
@Tag(name = "Agendamentos", description = "Criacao, cancelamento, reagendamento e gestao de agendamentos")
public class AgendamentoController {

    private final CriarAgendamentoUseCase criarAgendamentoUseCase;
    private final CancelarAgendamentoUseCase cancelarAgendamentoUseCase;
    private final ListarAgendamentosUseCase listarAgendamentosUseCase;
    private final AtualizarStatusAgendamentoUseCase atualizarStatusAgendamentoUseCase;
    private final ReagendarAgendamentoUseCase reagendarAgendamentoUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AgendamentoResponse criar(@RequestBody @Valid CriarAgendamentoRequest request) {
        var clienteId = AutenticacaoUtils.usuarioAutenticadoId();
        return AgendamentoResponse.from(criarAgendamentoUseCase.executar(request, clienteId));
    }

    @GetMapping("/meus")
    public List<AgendamentoResponse> meusAgendamentos() {
        var clienteId = AutenticacaoUtils.usuarioAutenticadoId();
        return listarAgendamentosUseCase.porCliente(clienteId).stream()
                .map(AgendamentoResponse::from)
                .toList();
    }

    @GetMapping("/estabelecimento/{estabelecimentoId}")
    public List<AgendamentoResponse> porEstabelecimento(@PathVariable UUID estabelecimentoId) {
        return listarAgendamentosUseCase.porEstabelecimento(estabelecimentoId).stream()
                .map(AgendamentoResponse::from)
                .toList();
    }

    @PatchMapping("/{id}/cancelar")
    public AgendamentoResponse cancelar(@PathVariable UUID id) {
        var clienteId = AutenticacaoUtils.usuarioAutenticadoId();
        return AgendamentoResponse.from(cancelarAgendamentoUseCase.executar(id, clienteId));
    }

    @PatchMapping("/{id}/status")
    public AgendamentoResponse atualizarStatus(@PathVariable UUID id, @RequestParam StatusAgendamento status) {
        return AgendamentoResponse.from(atualizarStatusAgendamentoUseCase.executar(id, status));
    }

    @PatchMapping("/{id}/reagendar")
    public AgendamentoResponse reagendar(@PathVariable UUID id, @RequestBody @Valid ReagendarRequest request) {
        var clienteId = AutenticacaoUtils.usuarioAutenticadoId();
        return AgendamentoResponse.from(reagendarAgendamentoUseCase.executar(id, clienteId, request));
    }
}
