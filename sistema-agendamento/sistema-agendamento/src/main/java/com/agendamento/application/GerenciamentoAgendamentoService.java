package com.agendamento.application;

import com.agendamento.domain.Agendamento;
import com.agendamento.domain.StatusAgendamento;
import com.agendamento.infrastructure.AgendamentoRepository;
import com.agendamento.application.exception.AgendamentoNaoEncontradoException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class GerenciamentoAgendamentoService {
    private final AgendamentoRepository repo;

    public GerenciamentoAgendamentoService(AgendamentoRepository repo) {
        this.repo = repo;
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
        agendamento.setDataHora(novaDataHora);
        agendamento.setStatus(StatusAgendamento.REAGENDADO);
        return repo.save(agendamento);
    }

    public Agendamento marcarNaoComparecimento(Long id) {
        Agendamento agendamento = repo.findById(id)
                .orElseThrow(() -> new AgendamentoNaoEncontradoException("Agendamento não encontrado"));
        agendamento.setStatus(StatusAgendamento.NAO_COMPARECEU);
        return repo.save(agendamento);
    }

    public List<Agendamento> listarPorEstabelecimento(Long estabelecimentoId) {
        return repo.findByEstabelecimentoId(estabelecimentoId);
    }

    public List<Agendamento> listarPorProfissional(Long profissionalId) {
        return repo.findByProfissionalId(profissionalId);
    }
}
