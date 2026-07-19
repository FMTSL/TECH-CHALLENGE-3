package br.com.fiap.agendamento.application.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Parametros de busca e filtragem avancada de estabelecimentos (feature 5 do desafio):
 * nome/localizacao, servico oferecido, faixa de preco, avaliacao minima e disponibilidade.
 * Todos os campos sao opcionais e combinaveis.
 */
public record FiltroBuscaEstabelecimento(
        String q,
        String servico,
        BigDecimal precoMin,
        BigDecimal precoMax,
        Double notaMinima,
        LocalDate disponivelEm
) {}
