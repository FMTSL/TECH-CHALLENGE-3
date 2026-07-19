package br.com.fiap.agendamento.domain.exception;

/** Lancada quando um recurso (agendamento, estabelecimento, etc.) nao e encontrado. */
public class RecursoNaoEncontradoException extends RuntimeException {
    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
