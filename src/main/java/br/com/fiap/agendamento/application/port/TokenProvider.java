package br.com.fiap.agendamento.application.port;

import br.com.fiap.agendamento.domain.model.Usuario;

/**
 * Port de saida (application -> infrastructure) para geracao/validacao de token.
 * A implementacao concreta (JWT/RS256 ou HS256) fica em infrastructure.security,
 * mantendo os use cases independentes do detalhe de tecnologia de autenticacao.
 */
public interface TokenProvider {
    String gerarToken(Usuario usuario);
}
