package com.agendamento.integration;

import com.agendamento.domain.Cliente;
import com.agendamento.infrastructure.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClienteRepository repo;

    private Cliente clienteBase;

    @BeforeEach
    void setup() {
        repo.deleteAll();
        clienteBase = new Cliente(null, "Felipe", "felipe@email.com");
        repo.save(clienteBase);
    }

    @Test
    void deveListarClientes() throws Exception {
        mockMvc.perform(get("/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nome", is("Felipe")));
    }

    @Test
    void deveBuscarPorId() throws Exception {
        mockMvc.perform(get("/clientes/" + clienteBase.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(clienteBase.getId().intValue())))
                .andExpect(jsonPath("$.nome", is("Felipe")));
    }

    @Test
    void deveCriarCliente() throws Exception {
        String body = "{ \"nome\": \"Maria\", \"email\": \"maria@email.com\" }";

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Maria")))
                .andExpect(jsonPath("$.email", is("maria@email.com")));
    }
}
