package br.com.fiap.agendamento.application.usecase.auth;

import br.com.fiap.agendamento.application.dto.RegistrarUsuarioRequest;
import br.com.fiap.agendamento.domain.exception.RegraDeNegocioException;
import br.com.fiap.agendamento.domain.model.Role;
import br.com.fiap.agendamento.domain.model.Usuario;
import br.com.fiap.agendamento.domain.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RegistrarUsuarioUseCase")
class RegistrarUsuarioUseCaseTest {

    @Mock private UsuarioRepository usuarioRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private RegistrarUsuarioUseCase useCase;

    @Test
    @DisplayName("deve registrar usuario com senha criptografada quando e-mail e novo")
    void deveRegistrarUsuarioComSucesso() {
        var request = new RegistrarUsuarioRequest("Maria", "maria@email.com", "senha123", Role.CLIENTE);

        when(usuarioRepository.existsByEmail(request.email())).thenReturn(false);
        when(passwordEncoder.encode(request.senha())).thenReturn("hash-seguro");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        Usuario resultado = useCase.executar(request);

        assertThat(resultado.getEmail()).isEqualTo("maria@email.com");
        assertThat(resultado.getSenhaHash()).isEqualTo("hash-seguro");
        assertThat(resultado.getRole()).isEqualTo(Role.CLIENTE);
        verify(passwordEncoder).encode("senha123");
    }

    @Test
    @DisplayName("deve rejeitar registro quando e-mail ja esta cadastrado")
    void deveRejeitarEmailDuplicado() {
        var request = new RegistrarUsuarioRequest("Maria", "maria@email.com", "senha123", Role.CLIENTE);
        when(usuarioRepository.existsByEmail(request.email())).thenReturn(true);

        assertThatThrownBy(() -> useCase.executar(request))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("Ja existe um usuario");

        verify(usuarioRepository, never()).save(any());
    }
}
