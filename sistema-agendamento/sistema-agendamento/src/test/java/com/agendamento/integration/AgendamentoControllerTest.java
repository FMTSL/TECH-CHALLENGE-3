package com.agendamento.integration;

import com.agendamento.domain.Agendamento;
import com.agendamento.domain.StatusAgendamento;
import com.agendamento.domain.Cliente;
import com.agendamento.domain.Estabelecimento;
import com.agendamento.domain.Profissional;
import com.agendamento.infrastructure.AgendamentoRepository;
import com.agendamento.infrastructure.ClienteRepository;
import com.agendamento.infrastructure.EstabelecimentoRepository;
import com.agendamento.infrastructure.ProfissionalRepository;
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

    @Autowired
    private ClienteRepository clienteRepo;

    @Autowired
    private EstabelecimentoRepository estRepo;

    @Autowired
    private ProfissionalRepository profRepo;

    private Agendamento agendamentoBase;

    @BeforeEach
    void setup() {

        repo.deleteAll();
        profRepo.deleteAll();
        estRepo.deleteAll();
        clienteRepo.deleteAll();

        Cliente cliente = clienteRepo.save(new Cliente(null, "Felipe", "felipe@email.com"));
        Estabelecimento est = estRepo.save(new Estabelecimento(null, "Salão Central", "Rua A, 123", "Corte", "08-18h", "foto.jpg"));
        Profissional prof = profRepo.save(new Profissional(null, "João", "Cabeleireiro", "08-18h", 100.0, est));

        agendamentoBase = new Agendamento(
                null,
                LocalDateTime.now().plusDays(1),
                StatusAgendamento.CONFIRMADO,
                prof,
                est,
                cliente
        );
        agendamentoBase = repo.save(agendamentoBase);
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
                "\"cliente\": {\"id\":" + agendamentoBase.getCliente().getId() + "}, " +
                "\"estabelecimento\": {\"id\":" + agendamentoBase.getEstabelecimento().getId() + "}, " +
                "\"profissional\": {\"id\":" + agendamentoBase.getProfissional().getId() + "} }";

        mockMvc.perform(post("/agendamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("CONFIRMADO")));
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