package br.com.fiap.agendamento.domain.exception;

/** Lancada quando uma regra de negocio do dominio e violada (ex: double-booking, cancelamento invalido). */
public class RegraDeNegocioException extends RuntimeException {
    public RegraDeNegocioException(String mensagem) {
        super(mensagem);
    }
}
