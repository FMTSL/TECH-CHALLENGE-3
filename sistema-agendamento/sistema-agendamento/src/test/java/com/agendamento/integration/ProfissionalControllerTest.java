package com.agendamento.integration;

import com.agendamento.domain.Estabelecimento;
import com.agendamento.domain.Profissional;
import com.agendamento.infrastructure.ProfissionalRepository;
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
class ProfissionalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProfissionalRepository repo;

    @BeforeEach
    void setup() {
        repo.deleteAll();
    }

    @Test
    void deveCadastrarProfissional() throws Exception {
        String json = "{ \"nome\":\"João\", \"especialidade\":\"Cabeleireiro\", " +
                "\"disponibilidade\":\"Seg-Sex 9h-18h\", \"tarifa\":100.0, " +
                "\"estabelecimento\": {\"nome\":\"Salão Central\",\"endereco\":\"Rua A\",\"servicos\":\"Corte\",\"horarios\":\"08-18h\",\"foto\":\"foto.jpg\"} }";

        mockMvc.perform(post("/profissionais")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("João"))
                .andExpect(jsonPath("$.especialidade").value("Cabeleireiro"));
    }

    @Test
    void deveListarProfissionais() throws Exception {
        Estabelecimento est = new Estabelecimento(null, "Studio Beleza", "Rua B", "Manicure", "09-19h", "foto2.jpg");
        repo.save(new Profissional(null, "Ana", "Manicure", "Seg-Sex 10h-19h", 80.0, est));

        mockMvc.perform(get("/profissionais"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nome", is("Ana")));
    }

    @Test
    void deveBuscarPorId() throws Exception {
        Estabelecimento est = new Estabelecimento(null, "Spa Relax", "Rua C", "Massagem", "08-20h", "foto3.jpg");
        Profissional prof = repo.save(new Profissional(null, "Carlos", "Massoterapeuta", "Seg-Sex 08h-20h", 120.0, est));

        mockMvc.perform(get("/profissionais/" + prof.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(prof.getId().intValue())))
                .andExpect(jsonPath("$.nome", is("Carlos")));
    }
}
