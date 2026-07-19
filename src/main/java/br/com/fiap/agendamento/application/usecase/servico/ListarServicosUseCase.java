package br.com.fiap.agendamento.application.usecase.servico;

import br.com.fiap.agendamento.domain.model.Servico;
import br.com.fiap.agendamento.domain.repository.ServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/** Caso de uso: lista servicos, opcionalmente filtrando por estabelecimento. */
@Service
@RequiredArgsConstructor
public class ListarServicosUseCase {

    private final ServicoRepository servicoRepository;

    public List<Servico> executar(UUID estabelecimentoId) {
        if (estabelecimentoId != null) {
            return servicoRepository.findByEstabelecimentoId(estabelecimentoId);
        }
        return servicoRepository.findAll();
    }
}
