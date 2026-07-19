package br.com.fiap.agendamento.application.usecase.servico;

import br.com.fiap.agendamento.application.dto.ServicoRequest;
import br.com.fiap.agendamento.domain.exception.RecursoNaoEncontradoException;
import br.com.fiap.agendamento.domain.model.Servico;
import br.com.fiap.agendamento.domain.repository.EstabelecimentoRepository;
import br.com.fiap.agendamento.domain.repository.ServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Caso de uso: cadastra um servico no catalogo de um estabelecimento existente. */
@Service
@RequiredArgsConstructor
public class CadastrarServicoUseCase {

    private final ServicoRepository servicoRepository;
    private final EstabelecimentoRepository estabelecimentoRepository;

    @Transactional
    public Servico executar(ServicoRequest request) {
        if (!estabelecimentoRepository.existsById(request.estabelecimentoId())) {
            throw new RecursoNaoEncontradoException("Estabelecimento nao encontrado: " + request.estabelecimentoId());
        }

        Servico servico = Servico.builder()
                .nome(request.nome())
                .descricao(request.descricao())
                .preco(request.preco())
                .duracaoMinutos(request.duracaoMinutos())
                .estabelecimentoId(request.estabelecimentoId())
                .build();

        return servicoRepository.save(servico);
    }
}
