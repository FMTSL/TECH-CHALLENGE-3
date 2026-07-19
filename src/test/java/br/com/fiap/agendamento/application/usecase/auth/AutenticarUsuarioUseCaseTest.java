package br.com.fiap.agendamento.application.usecase.auth;

import br.com.fiap.agendamento.application.dto.LoginRequest;
import br.com.fiap.agendamento.application.port.TokenProvider;
import br.com.fiap.agendamento.domain.exception.CredenciaisInvalidasException;
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

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AutenticarUsuarioUseCase")
class AutenticarUsuarioUseCaseTest {

    @Mock private UsuarioRepository usuarioRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private TokenProvider tokenProvider;

    @InjectMocks
    private AutenticarUsuarioUseCase useCase;

    @Test
    @DisplayName("deve retornar token quando credenciais sao validas")
    void deveAutenticarComSucesso() {
        var usuario = Usuario.builder().id(UUID.randomUUID()).email("cliente@email.com")
                .senhaHash("hash").role(Role.CLIENTE).build();
        var request = new LoginRequest("cliente@email.com", "senha123");

        when(usuarioRepository.findByEmail("cliente@email.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senha123", "hash")).thenReturn(true);
        when(tokenProvider.gerarToken(usuario)).thenReturn("token-jwt");

        String token = useCase.executar(request);

        assertThat(token).isEqualTo("token-jwt");
    }

    @Test
    @DisplayName("deve rejeitar quando e-mail nao existe")
    void deveRejeitarEmailInexistente() {
        var request = new LoginRequest("naoexiste@email.com", "senha123");
        when(usuarioRepository.findByEmail("naoexiste@email.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.executar(request))
                .isInstanceOf(CredenciaisInvalidasException.class);
    }

    @Test
    @DisplayName("deve rejeitar quando senha esta incorreta")
    void deveRejeitarSenhaIncorreta() {
        var usuario = Usuario.builder().id(UUID.randomUUID()).email("cliente@email.com")
                .senhaHash("hash").role(Role.CLIENTE).build();
        var request = new LoginRequest("cliente@email.com", "senhaErrada");

        when(usuarioRepository.findByEmail("cliente@email.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senhaErrada", "hash")).thenReturn(false);

        assertThatThrownBy(() -> useCase.executar(request))
                .isInstanceOf(CredenciaisInvalidasException.class);
    }
}
