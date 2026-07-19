package br.com.fiap.agendamento.application.usecase.estabelecimento;

import br.com.fiap.agendamento.application.dto.FiltroBuscaEstabelecimento;
import br.com.fiap.agendamento.application.usecase.disponibilidade.ConsultarDisponibilidadeUseCase;
import br.com.fiap.agendamento.domain.exception.RecursoNaoEncontradoException;
import br.com.fiap.agendamento.domain.model.Avaliacao;
import br.com.fiap.agendamento.domain.model.Estabelecimento;
import br.com.fiap.agendamento.domain.model.Profissional;
import br.com.fiap.agendamento.domain.repository.AvaliacaoRepository;
import br.com.fiap.agendamento.domain.repository.EstabelecimentoRepository;
import br.com.fiap.agendamento.domain.repository.ProfissionalRepository;
import br.com.fiap.agendamento.domain.repository.ServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Caso de uso: busca e filtragem avancada de estabelecimentos (feature 5 do desafio) -
 * por nome/localizacao, servico oferecido, faixa de preco, avaliacao minima e
 * disponibilidade em uma data.
 *
 * Trade-off: os filtros sao aplicados em memoria sobre o resultado da busca textual,
 * em vez de uma unica query JPQL com varios joins. Para o volume de dados de um
 * desafio academico isso e simples, legivel e facil de testar; em producao com
 * grande volume, a evolucao natural seria um indice de busca dedicado (Elasticsearch),
 * como fez o projeto de referencia usado como modelo.
 */
@Service
@RequiredArgsConstructor
public class BuscarEstabelecimentosUseCase {

    private final EstabelecimentoRepository estabelecimentoRepository;
    private final ServicoRepository servicoRepository;
    private final AvaliacaoRepository avaliacaoRepository;
    private final ProfissionalRepository profissionalRepository;
    private final ConsultarDisponibilidadeUseCase consultarDisponibilidadeUseCase;

    public List<Estabelecimento> executar(FiltroBuscaEstabelecimento filtro) {
        List<Estabelecimento> base = (filtro.q() == null || filtro.q().isBlank())
                ? estabelecimentoRepository.findAll()
                : estabelecimentoRepository.findByNomeContainingIgnoreCaseOrCidadeContainingIgnoreCase(filtro.q(), filtro.q());

        return base.stream()
                .filter(e -> filtro.servico() == null || filtro.servico().isBlank()
                        || servicoRepository.existsByEstabelecimentoIdAndNomeContainingIgnoreCase(e.getId(), filtro.servico()))
                .filter(e -> (filtro.precoMin() == null && filtro.precoMax() == null)
                        || servicoRepository.existsByEstabelecimentoIdAndPrecoBetween(
                                e.getId(),
                                filtro.precoMin() != null ? filtro.precoMin() : java.math.BigDecimal.ZERO,
                                filtro.precoMax() != null ? filtro.precoMax() : new java.math.BigDecimal("999999")))
                .filter(e -> filtro.notaMinima() == null || notaMediaOuZero(e.getId()) >= filtro.notaMinima())
                .filter(e -> filtro.disponivelEm() == null || temProfissionalDisponivel(e.getId(), filtro.disponivelEm()))
                .toList();
    }

    public Estabelecimento buscarPorId(UUID id) {
        return estabelecimentoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Estabelecimento nao encontrado: " + id));
    }

    /** Nota media (1-5) das avaliacoes do estabelecimento; {@code null} se ainda nao tem nenhuma. */
    public Double notaMedia(UUID estabelecimentoId) {
        List<Avaliacao> avaliacoes = avaliacaoRepository.findByEstabelecimentoId(estabelecimentoId);
        if (avaliacoes.isEmpty()) {
            return null;
        }
        return avaliacoes.stream().mapToInt(Avaliacao::getNota).average().orElse(0.0);
    }

    /** Mesma nota media, mas como 0.0 (nao null) para uso direto em comparacoes de filtro. */
    private double notaMediaOuZero(UUID estabelecimentoId) {
        Double media = notaMedia(estabelecimentoId);
        return media != null ? media : 0.0;
    }

    private boolean temProfissionalDisponivel(UUID estabelecimentoId, java.time.LocalDate data) {
        List<Profissional> profissionais = profissionalRepository.findByEstabelecimentoId(estabelecimentoId);
        return profissionais.stream()
                .anyMatch(p -> !consultarDisponibilidadeUseCase.executar(p.getId(), data).isEmpty());
    }
}
