package br.com.fiap.agendamento.infrastructure.web.controller;

import br.com.fiap.agendamento.application.dto.DisponibilidadeRequest;
import br.com.fiap.agendamento.application.dto.DisponibilidadeResponse;
import br.com.fiap.agendamento.application.usecase.disponibilidade.ConsultarDisponibilidadeUseCase;
import br.com.fiap.agendamento.application.usecase.disponibilidade.DefinirDisponibilidadeUseCase;
import br.com.fiap.agendamento.domain.repository.HorarioDisponivelRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/** Perfil de profissional: cadastro e consulta de agenda/disponibilidade (feature 2 do desafio). */
@RestController
@RequestMapping("/api/profissionais/{profissionalId}/disponibilidades")
@RequiredArgsConstructor
@Tag(name = "Disponibilidade", description = "Agenda semanal e horarios livres dos profissionais")
public class DisponibilidadeController {

    private final DefinirDisponibilidadeUseCase definirDisponibilidadeUseCase;
    private final ConsultarDisponibilidadeUseCase consultarDisponibilidadeUseCase;
    private final HorarioDisponivelRepository horarioDisponivelRepository;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DisponibilidadeResponse definir(@PathVariable UUID profissionalId,
                                            @RequestBody @Valid DisponibilidadeRequest request) {
        return DisponibilidadeResponse.from(definirDisponibilidadeUseCase.executar(profissionalId, request));
    }

    @GetMapping
    public List<DisponibilidadeResponse> listarJanelas(@PathVariable UUID profissionalId) {
        return horarioDisponivelRepository.findByProfissionalId(profissionalId).stream()
                .map(DisponibilidadeResponse::from)
                .toList();
    }

    @GetMapping("/slots-livres")
    public List<LocalDateTime> slotsLivres(@PathVariable UUID profissionalId,
                                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        return consultarDisponibilidadeUseCase.executar(profissionalId, data);
    }
}
