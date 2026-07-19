package br.com.fiap.agendamento.application.usecase.profissional;

import br.com.fiap.agendamento.domain.model.Profissional;
import br.com.fiap.agendamento.domain.repository.ProfissionalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/** Caso de uso: lista profissionais, opcionalmente filtrando por estabelecimento. */
@Service
@RequiredArgsConstructor
public class ListarProfissionaisUseCase {

    private final ProfissionalRepository profissionalRepository;

    public List<Profissional> executar(UUID estabelecimentoId) {
        if (estabelecimentoId != null) {
            return profissionalRepository.findByEstabelecimentoId(estabelecimentoId);
        }
        return profissionalRepository.findAll();
    }
}
