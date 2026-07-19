package br.com.fiap.agendamento.application.usecase.estabelecimento;

import br.com.fiap.agendamento.application.dto.EstabelecimentoRequest;
import br.com.fiap.agendamento.domain.model.Estabelecimento;
import br.com.fiap.agendamento.domain.repository.EstabelecimentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/** Caso de uso: cadastra um estabelecimento vinculado ao usuario dono autenticado (feature 1). */
@Service
@RequiredArgsConstructor
public class CadastrarEstabelecimentoUseCase {

    private final EstabelecimentoRepository estabelecimentoRepository;

    @Transactional
    public Estabelecimento executar(EstabelecimentoRequest request, UUID usuarioDonoId) {
        Estabelecimento estabelecimento = Estabelecimento.builder()
                .nome(request.nome())
                .endereco(request.endereco())
                .cidade(request.cidade())
                .horarioFuncionamento(request.horarioFuncionamento())
                .fotos(request.fotos())
                .usuarioDonoId(usuarioDonoId)
                .build();

        return estabelecimentoRepository.save(estabelecimento);
    }
}
