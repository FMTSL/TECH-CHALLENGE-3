package br.com.fiap.agendamento.infrastructure.web.exception;

import br.com.fiap.agendamento.application.dto.ErroResponse;
import br.com.fiap.agendamento.domain.exception.CredenciaisInvalidasException;
import br.com.fiap.agendamento.domain.exception.RecursoNaoEncontradoException;
import br.com.fiap.agendamento.domain.exception.RegraDeNegocioException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

/** Centraliza a traducao de excecoes de dominio/aplicacao para respostas HTTP consistentes. */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResponse> handleNaoEncontrado(RecursoNaoEncontradoException ex, HttpServletRequest req) {
        return corpo(HttpStatus.NOT_FOUND, ex.getMessage(), req);
    }

    @ExceptionHandler(RegraDeNegocioException.class)
    public ResponseEntity<ErroResponse> handleRegraDeNegocio(RegraDeNegocioException ex, HttpServletRequest req) {
        return corpo(HttpStatus.CONFLICT, ex.getMessage(), req);
    }

    @ExceptionHandler(CredenciaisInvalidasException.class)
    public ResponseEntity<ErroResponse> handleCredenciais(CredenciaisInvalidasException ex, HttpServletRequest req) {
        return corpo(HttpStatus.UNAUTHORIZED, ex.getMessage(), req);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> handleValidacao(MethodArgumentNotValidException ex, HttpServletRequest req) {
        String mensagem = ex.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return corpo(HttpStatus.BAD_REQUEST, mensagem, req);
    }

    /**
     * Rede de seguranca para qualquer excecao nao mapeada explicitamente acima (bugs de
     * infraestrutura, erros de mapeamento JPA, etc). Sem isso, uma excecao inesperada escapa
     * direto para o container de servlet, que pode devolver uma resposta pouco informativa
     * (sem corpo, com um status confuso) em vez de um 500 claro com detalhe do erro - o que
     * torna qualquer bug futuro desse tipo muito mais dificil de diagnosticar pelo cliente da API.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> handleInesperada(Exception ex, HttpServletRequest req) {
        log.error("Erro nao tratado em {} {}", req.getMethod(), req.getRequestURI(), ex);
        return corpo(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno inesperado. Consulte os logs do servidor.", req);
    }

    private ResponseEntity<ErroResponse> corpo(HttpStatus status, String mensagem, HttpServletRequest req) {
        var erro = new ErroResponse(Instant.now(), status.value(), status.getReasonPhrase(), mensagem, req.getRequestURI());
        return ResponseEntity.status(status).body(erro);
    }
}
