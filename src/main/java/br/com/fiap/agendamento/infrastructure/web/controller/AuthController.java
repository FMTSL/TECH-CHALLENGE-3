package br.com.fiap.agendamento.infrastructure.web.controller;

import br.com.fiap.agendamento.application.dto.LoginRequest;
import br.com.fiap.agendamento.application.dto.RegistrarUsuarioRequest;
import br.com.fiap.agendamento.application.dto.TokenResponse;
import br.com.fiap.agendamento.application.usecase.auth.AutenticarUsuarioUseCase;
import br.com.fiap.agendamento.application.usecase.auth.RegistrarUsuarioUseCase;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** Endpoints REST de registro e login de usuarios (emissao de JWT). */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticacao", description = "Registro e login de usuarios")
public class AuthController {

    private final RegistrarUsuarioUseCase registrarUsuarioUseCase;
    private final AutenticarUsuarioUseCase autenticarUsuarioUseCase;

    @PostMapping("/registrar")
    @ResponseStatus(HttpStatus.CREATED)
    public void registrar(@RequestBody @Valid RegistrarUsuarioRequest request) {
        registrarUsuarioUseCase.executar(request);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody @Valid LoginRequest request) {
        String token = autenticarUsuarioUseCase.executar(request);
        return ResponseEntity.ok(TokenResponse.of(token));
    }
}
