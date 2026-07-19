package br.com.fiap.agendamento.application.usecase.auth;

import br.com.fiap.agendamento.application.dto.LoginRequest;
import br.com.fiap.agendamento.application.port.TokenProvider;
import br.com.fiap.agendamento.domain.exception.CredenciaisInvalidasException;
import br.com.fiap.agendamento.domain.model.Usuario;
import br.com.fiap.agendamento.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/** Caso de uso: autenticar um usuario e emitir um token JWT. */
@Service
@RequiredArgsConstructor
public class AutenticarUsuarioUseCase {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    public String executar(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.email())
                .orElseThrow(() -> new CredenciaisInvalidasException("E-mail ou senha invalidos"));

        if (!passwordEncoder.matches(request.senha(), usuario.getSenhaHash())) {
            throw new CredenciaisInvalidasException("E-mail ou senha invalidos");
        }

        return tokenProvider.gerarToken(usuario);
    }
}
