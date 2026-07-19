package br.com.fiap.agendamento.infrastructure.web;

import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

/** Extrai o id do usuario autenticado a partir do SecurityContext (populado pelo JwtAuthenticationFilter). */
public final class AutenticacaoUtils {

    private AutenticacaoUtils() {}

    public static UUID usuarioAutenticadoId() {
        return (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
