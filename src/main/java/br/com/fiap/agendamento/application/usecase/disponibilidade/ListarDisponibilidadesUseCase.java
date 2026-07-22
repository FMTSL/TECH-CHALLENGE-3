package br.com.fiap.agendamento.application.usecase.disponibilidade;

import br.com.fiap.agendamento.domain.model.HorarioDisponivel;
import br.com.fiap.agendamento.domain.repository.HorarioDisponivelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/** Caso de uso: lista as janelas de disponibilidade recorrente cadastradas para um profissional. */
@Service
@RequiredArgsConstructor
public class ListarDisponibilidadesUseCase {

    private final HorarioDisponivelRepository horarioDisponivelRepository;

    public List<HorarioDisponivel> executar(UUID profissionalId) {
        return horarioDisponivelRepository.findByProfissionalId(profissionalId);
    }
}
