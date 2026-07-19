package br.com.fiap.agendamento.domain.exception;

/** Lancada quando e-mail/senha informados no login nao conferem. */
public class CredenciaisInvalidasException extends RuntimeException {
    public CredenciaisInvalidasException(String mensagem) {
        super(mensagem);
    }
}
