package br.com.fiap.agendamento.infrastructure.web;

import br.com.fiap.agendamento.TestcontainersConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Teste de integracao (BDD-lite / caminho feliz) subindo a aplicacao completa
 * com um Postgres real via Testcontainers. Valida registro + login end-to-end,
 * exercitando controller -> use case -> repositorio -> banco.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
class FluxoAgendamentoIT {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Test
    void deveRegistrarELogarUsuarioComSucesso() throws Exception {
        var registro = Map.of(
                "nome", "Cliente Teste",
                "email", "cliente.it@email.com",
                "senha", "senha123",
                "role", "CLIENTE"
        );

        mockMvc.perform(post("/api/auth/registrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registro)))
                .andExpect(status().isCreated());

        var login = Map.of("email", "cliente.it@email.com", "senha", "senha123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk());
    }

    @Test
    void deveRejeitarLoginComSenhaIncorreta() throws Exception {
        var registro = Map.of(
                "nome", "Outro Cliente",
                "email", "outro.it@email.com",
                "senha", "senhaCorreta",
                "role", "CLIENTE"
        );
        mockMvc.perform(post("/api/auth/registrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registro)))
                .andExpect(status().isCreated());

        var loginErrado = Map.of("email", "outro.it@email.com", "senha", "senhaErrada");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginErrado)))
                .andExpect(status().isUnauthorized());
    }
}
