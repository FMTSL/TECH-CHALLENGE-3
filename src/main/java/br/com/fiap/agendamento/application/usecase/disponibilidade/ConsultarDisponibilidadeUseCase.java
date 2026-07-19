package br.com.fiap.agendamento.application.usecase.disponibilidade;

import br.com.fiap.agendamento.domain.model.Agendamento;
import br.com.fiap.agendamento.domain.model.DiaSemana;
import br.com.fiap.agendamento.domain.model.HorarioDisponivel;
import br.com.fiap.agendamento.domain.model.StatusAgendamento;
import br.com.fiap.agendamento.domain.repository.AgendamentoRepository;
import br.com.fiap.agendamento.domain.repository.HorarioDisponivelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Caso de uso: calcula os horarios livres de um profissional em uma data especifica,
 * cruzando as janelas de disponibilidade cadastradas com os agendamentos ja existentes.
 * Granularidade fixa de slot: 30 minutos (trade-off documentado no README).
 */
@Service
@RequiredArgsConstructor
public class ConsultarDisponibilidadeUseCase {

    private static final int GRANULARIDADE_MINUTOS = 30;

    private final HorarioDisponivelRepository horarioDisponivelRepository;
    private final AgendamentoRepository agendamentoRepository;

    public List<LocalDateTime> executar(UUID profissionalId, LocalDate data) {
        DiaSemana diaSemana = DiaSemana.fromJavaDayOfWeek(data.getDayOfWeek());

        List<HorarioDisponivel> janelas = horarioDisponivelRepository
                .findByProfissionalIdAndDiaSemana(profissionalId, diaSemana);

        if (janelas.isEmpty()) {
            return List.of();
        }

        Set<LocalDateTime> ocupados = agendamentoRepository
                .findByProfissionalIdAndDataHoraBetween(profissionalId, data.atStartOfDay(), data.atTime(LocalTime.MAX))
                .stream()
                .filter(a -> a.getStatus() != StatusAgendamento.CANCELADO)
                .map(Agendamento::getDataHora)
                .collect(Collectors.toSet());

        List<LocalDateTime> livres = new ArrayList<>();
        for (HorarioDisponivel janela : janelas) {
            LocalTime cursor = janela.getHoraInicio();
            while (cursor.isBefore(janela.getHoraFim())) {
                LocalDateTime slot = data.atTime(cursor);
                if (!ocupados.contains(slot) && slot.isAfter(LocalDateTime.now())) {
                    livres.add(slot);
                }
                cursor = cursor.plusMinutes(GRANULARIDADE_MINUTOS);
            }
        }
        return livres;
    }

    /** Usado pela validacao de criacao/reagendamento de agendamento: o horario pedido esta dentro de alguma janela? */
    public boolean estaDentroDeAlgumaJanela(UUID profissionalId, LocalDateTime dataHora) {
        DiaSemana diaSemana = DiaSemana.fromJavaDayOfWeek(dataHora.getDayOfWeek());
        List<HorarioDisponivel> janelas = horarioDisponivelRepository
                .findByProfissionalIdAndDiaSemana(profissionalId, diaSemana);

        if (janelas.isEmpty()) {
            // Profissional ainda nao configurou agenda: nao bloqueamos o agendamento (compatibilidade).
            return true;
        }
        return janelas.stream().anyMatch(j -> j.contemHorario(dataHora.toLocalTime()));
    }
}
