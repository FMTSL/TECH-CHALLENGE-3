package br.com.fiap.agendamento.application.usecase.profissional;

import br.com.fiap.agendamento.application.dto.ProfissionalRequest;
import br.com.fiap.agendamento.domain.exception.RecursoNaoEncontradoException;
import br.com.fiap.agendamento.domain.model.Profissional;
import br.com.fiap.agendamento.domain.repository.EstabelecimentoRepository;
import br.com.fiap.agendamento.domain.repository.ProfissionalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Caso de uso: cadastra o perfil de um profissional vinculado a um estabelecimento existente (feature 2). */
@Service
@RequiredArgsConstructor
public class CadastrarProfissionalUseCase {

    private final ProfissionalRepository profissionalRepository;
    private final EstabelecimentoRepository estabelecimentoRepository;

    @Transactional
    public Profissional executar(ProfissionalRequest request) {
        if (!estabelecimentoRepository.existsById(request.estabelecimentoId())) {
            throw new RecursoNaoEncontradoException("Estabelecimento nao encontrado: " + request.estabelecimentoId());
        }

        Profissional profissional = Profissional.builder()
                .nome(request.nome())
                .especialidades(request.especialidades())
                .tarifaBase(request.tarifaBase())
                .estabelecimentoId(request.estabelecimentoId())
                .emailContato(request.emailContato())
                .build();

        return profissionalRepository.save(profissional);
    }
}
