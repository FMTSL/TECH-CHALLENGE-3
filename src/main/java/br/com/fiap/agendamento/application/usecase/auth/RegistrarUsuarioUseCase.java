package br.com.fiap.agendamento.application.usecase.auth;

import br.com.fiap.agendamento.application.dto.RegistrarUsuarioRequest;
import br.com.fiap.agendamento.domain.exception.RegraDeNegocioException;
import br.com.fiap.agendamento.domain.model.Usuario;
import br.com.fiap.agendamento.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso: registrar um novo usuario (cliente ou dono de estabelecimento).
 * Responsabilidade unica (SRP): apenas orquestra a criacao de um usuario valido.
 */
@Service
@RequiredArgsConstructor
public class RegistrarUsuarioUseCase {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Usuario executar(RegistrarUsuarioRequest request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new RegraDeNegocioException("Ja existe um usuario cadastrado com este e-mail");
        }

        Usuario usuario = Usuario.builder()
                .nome(request.nome())
                .email(request.email())
                .senhaHash(passwordEncoder.encode(request.senha()))
                .role(request.role())
                .build();

        return usuarioRepository.save(usuario);
    }
}
