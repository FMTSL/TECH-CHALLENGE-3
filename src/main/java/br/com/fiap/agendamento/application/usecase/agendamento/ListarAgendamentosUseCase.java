package br.com.fiap.agendamento.application.usecase.agendamento;

import br.com.fiap.agendamento.domain.model.Agendamento;
import br.com.fiap.agendamento.domain.repository.AgendamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/** Caso de uso: lista agendamentos por cliente ("meus agendamentos") ou por estabelecimento (painel de gestao, feature 6). */
@Service
@RequiredArgsConstructor
public class ListarAgendamentosUseCase {

    private final AgendamentoRepository agendamentoRepository;

    public List<Agendamento> porCliente(UUID clienteId) {
        return agendamentoRepository.findByClienteId(clienteId);
    }

    public List<Agendamento> porEstabelecimento(UUID estabelecimentoId) {
        return agendamentoRepository.findByEstabelecimentoId(estabelecimentoId);
    }
}
