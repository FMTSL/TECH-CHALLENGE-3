package br.com.fiap.agendamento.infrastructure.security;

import br.com.fiap.agendamento.application.port.TokenProvider;
import br.com.fiap.agendamento.domain.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

/**
 * Implementacao concreta do port TokenProvider usando JJWT (HS256).
 * Fica isolada em infrastructure.security - os use cases nao sabem que a
 * tecnologia por tras da autenticacao e JWT.
 */
@Component
public class JwtService implements TokenProvider {

    private final SecretKey chave;
    private final long expiracaoMs;

    public JwtService(
            @Value("${app.security.jwt.secret}") String segredo,
            @Value("${app.security.jwt.expiracao-ms:86400000}") long expiracaoMs) {
        this.chave = Keys.hmacShaKeyFor(segredo.getBytes(StandardCharsets.UTF_8));
        this.expiracaoMs = expiracaoMs;
    }

    @Override
    public String gerarToken(Usuario usuario) {
        Instant agora = Instant.now();
        return Jwts.builder()
                .subject(usuario.getId().toString())
                .claim("email", usuario.getEmail())
                .claim("role", usuario.getRole().name())
                .issuedAt(Date.from(agora))
                .expiration(Date.from(agora.plusMillis(expiracaoMs)))
                .signWith(chave)
                .compact();
    }

    public Claims validarEExtrairClaims(String token) {
        return Jwts.parser()
                .verifyWith(chave)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
