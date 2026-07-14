package com.agendamento.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class GerenciamentoAgendamentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deveReagendarAgendamento() throws Exception {
        String novaData = "2026-07-15T14:00:00";

        mockMvc.perform(put("/gerenciamento/agendamentos/1/reagendar")
                        .param("novaDataHora", novaData)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REAGENDADO"));
    }
}
