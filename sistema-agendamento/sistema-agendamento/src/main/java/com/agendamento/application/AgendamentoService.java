package com.agendamento.application;

import com.agendamento.domain.Agendamento;
import com.agendamento.domain.StatusAgendamento;
import com.agendamento.application.exception.AgendamentoNaoEncontradoException;
import com.agendamento.application.exception.HorarioOcupadoException;
import com.agendamento.infrastructure.AgendamentoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AgendamentoService {
    private final AgendamentoRepository repo;

    public AgendamentoService(AgendamentoRepository repo) {
        this.repo = repo;
    }

    public Agendamento criar(Agendamento agendamento) {
        List<Agendamento> existentes = repo.findByProfissionalIdAndDataHora(
                agendamento.getProfissional().getId(),
                agendamento.getDataHora()
        );
        if (!existentes.isEmpty()) {
            throw new HorarioOcupadoException("Horário já ocupado para este profissional.");
        }
        agendamento.setStatus(StatusAgendamento.CONFIRMADO);
        return repo.save(agendamento);
    }

    public List<Agendamento> listarTodos() {
        return repo.findAll();
    }

    public Optional<Agendamento> buscarPorId(Long id) {
        return repo.findById(id);
    }

    public Agendamento cancelar(Long id) {
        Agendamento agendamento = repo.findById(id)
                .orElseThrow(() -> new AgendamentoNaoEncontradoException("Agendamento não encontrado"));
        agendamento.setStatus(StatusAgendamento.CANCELADO);
        return repo.save(agendamento);
    }

    public Agendamento reagendar(Long id, LocalDateTime novaDataHora) {
        Agendamento agendamento = repo.findById(id)
                .orElseThrow(() -> new AgendamentoNaoEncontradoException("Agendamento não encontrado"));

        List<Agendamento> existentes = repo.findByProfissionalIdAndDataHora(
                agendamento.getProfissional().getId(),
                novaDataHora
        );
        if (!existentes.isEmpty()) {
            throw new HorarioOcupadoException("Horário já ocupado para este profissional.");
        }

        agendamento.setDataHora(novaDataHora);
        agendamento.setStatus(StatusAgendamento.REAGENDADO);
        return repo.save(agendamento);
    }
}