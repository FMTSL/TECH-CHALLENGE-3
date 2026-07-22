package br.com.fiap.agendamento.infrastructure.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/** Intercepta requisicoes, valida o JWT (se presente) e popula o SecurityContext. */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            try {
                Claims claims = jwtService.validarEExtrairClaims(header.substring(7));
                UUID usuarioId = UUID.fromString(claims.getSubject());
                String role = claims.get("role", String.class);

                var authentication = new UsernamePasswordAuthenticationToken(
                        usuarioId, null, List.of(new SimpleGrantedAuthority("ROLE_" + role)));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                log.warn("Falha ao validar JWT em {} {}: {} - {}",
                        request.getMethod(), request.getRequestURI(), e.getClass().getSimpleName(), e.getMessage());
                SecurityContextHolder.clearContext();
            }
        } else if (header != null) {
            log.warn("Cabecalho Authorization presente mas sem prefixo 'Bearer ' em {} {}",
                    request.getMethod(), request.getRequestURI());
        }

        filterChain.doFilter(request, response);
    }
}
