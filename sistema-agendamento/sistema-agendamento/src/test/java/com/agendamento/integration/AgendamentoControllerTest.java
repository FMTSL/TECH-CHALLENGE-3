package com.agendamento.integration;

import com.agendamento.domain.Agendamento;
import com.agendamento.domain.StatusAgendamento;
import com.agendamento.domain.Cliente;
import com.agendamento.domain.Estabelecimento;
import com.agendamento.domain.Profissional;
import com.agendamento.infrastructure.AgendamentoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AgendamentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AgendamentoRepository repo;

    private Agendamento agendamentoBase;

    @BeforeEach
    void setup() {
        repo.deleteAll();

        Cliente cliente = new Cliente(null, "Felipe", "felipe@email.com");
        Estabelecimento est = new Estabelecimento(null, "Salão Central", "Rua A, 123", "Corte", "08-18h", "foto.jpg");
        Profissional prof = new Profissional(null, "João", "Cabeleireiro", "08-18h", 100.0, est);

        agendamentoBase = new Agendamento(
                null,
                LocalDateTime.now().plusDays(1),
                StatusAgendamento.CONFIRMADO,
                prof,
                est,
                cliente
        );
        repo.save(agendamentoBase);
    }

    @Test
    void deveListarAgendamentos() throws Exception {
        mockMvc.perform(get("/agendamentos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].status", is("CONFIRMADO")));
    }

    @Test
    void deveBuscarPorId() throws Exception {
        mockMvc.perform(get("/agendamentos/" + agendamentoBase.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(agendamentoBase.getId().intValue())));
    }

    @Test
    void deveCriarAgendamento() throws Exception {
        String body = "{ \"dataHora\": \"" + LocalDateTime.now().plusDays(3) + "\", " +
                "\"status\": \"CONFIRMADO\", " +
                "\"cliente\": {\"nome\":\"Maria\",\"email\":\"maria@email.com\"}, " +
                "\"estabelecimento\": {\"nome\":\"Studio Beleza\",\"endereco\":\"Rua B\",\"servicos\":\"Manicure\",\"horarios\":\"09-19h\",\"foto\":\"foto2.jpg\"}, " +
                "\"profissional\": {\"nome\":\"Ana\",\"especialidade\":\"Manicure\",\"disponibilidade\":\"Seg-Sex 10h-19h\",\"tarifa\":80.0} }";

        mockMvc.perform(post("/agendamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("CONFIRMADO")))
                .andExpect(jsonPath("$.cliente.nome", is("Maria")));
    }

    @Test
    void deveCancelarAgendamento() throws Exception {
        mockMvc.perform(delete("/agendamentos/" + agendamentoBase.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("CANCELADO")));
    }

    @Test
    void deveReagendarAgendamento() throws Exception {
        LocalDateTime novaData = LocalDateTime.now().plusDays(2);
        String body = "{ \"dataHora\": \"" + novaData + "\" }";

        mockMvc.perform(put("/agendamentos/" + agendamentoBase.getId() + "/reagendar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("REAGENDADO")));
    }
}
