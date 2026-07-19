package br.com.fiap.agendamento.application.usecase.disponibilidade;

import br.com.fiap.agendamento.application.dto.DisponibilidadeRequest;
import br.com.fiap.agendamento.domain.exception.RecursoNaoEncontradoException;
import br.com.fiap.agendamento.domain.exception.RegraDeNegocioException;
import br.com.fiap.agendamento.domain.model.HorarioDisponivel;
import br.com.fiap.agendamento.domain.repository.HorarioDisponivelRepository;
import br.com.fiap.agendamento.domain.repository.ProfissionalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/** Caso de uso: profissional/estabelecimento cadastra uma janela recorrente de disponibilidade. */
@Service
@RequiredArgsConstructor
public class DefinirDisponibilidadeUseCase {

    private final HorarioDisponivelRepository horarioDisponivelRepository;
    private final ProfissionalRepository profissionalRepository;

    @Transactional
    public HorarioDisponivel executar(UUID profissionalId, DisponibilidadeRequest request) {
        if (!profissionalRepository.existsById(profissionalId)) {
            throw new RecursoNaoEncontradoException("Profissional nao encontrado: " + profissionalId);
        }
        if (!request.horaInicio().isBefore(request.horaFim())) {
            throw new RegraDeNegocioException("horaInicio deve ser anterior a horaFim");
        }

        HorarioDisponivel horario = HorarioDisponivel.builder()
                .profissionalId(profissionalId)
                .diaSemana(request.diaSemana())
                .horaInicio(request.horaInicio())
                .horaFim(request.horaFim())
                .build();

        return horarioDisponivelRepository.save(horario);
    }
}
