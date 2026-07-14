package com.agendamento.integration;

import com.agendamento.domain.Estabelecimento;
import com.agendamento.infrastructure.EstabelecimentoRepository;
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
class EstabelecimentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EstabelecimentoRepository repo;

    @BeforeEach
    void setup() {
        repo.deleteAll();
    }

    @Test
    void deveCadastrarEstabelecimento() throws Exception {
        String json = "{ \"nome\":\"Salão Beleza\", \"endereco\":\"Rua A\", " +
                "\"servicos\":\"Corte\", \"horarios\":\"9-18h\", \"foto\":\"foto.png\" }";

        mockMvc.perform(post("/estabelecimentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Salão Beleza"))
                .andExpect(jsonPath("$.endereco").value("Rua A"));
    }

    @Test
    void deveListarEstabelecimentos() throws Exception {
        repo.save(new Estabelecimento(null, "Studio Beleza", "Rua B", "Manicure", "10-19h", "foto2.png"));

        mockMvc.perform(get("/estabelecimentos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nome", is("Studio Beleza")));
    }

    @Test
    void deveBuscarPorId() throws Exception {
        Estabelecimento est = repo.save(new Estabelecimento(null, "Spa Relax", "Rua C", "Massagem", "08-20h", "foto3.png"));

        mockMvc.perform(get("/estabelecimentos/" + est.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(est.getId().intValue())))
                .andExpect(jsonPath("$.nome", is("Spa Relax")));
    }
}
