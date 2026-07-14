package com.agendamento.unit;

import com.agendamento.application.ProfissionalService;
import com.agendamento.domain.Profissional;
import com.agendamento.infrastructure.ProfissionalRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProfissionalServiceTest {

    @Test
    void deveSalvarProfissional() {
        ProfissionalRepository repo = Mockito.mock(ProfissionalRepository.class);
        ProfissionalService service = new ProfissionalService(repo);

        Profissional p = new Profissional(null, "João", "Cabeleireiro", "Seg-Sex 9h-18h", 100.0, null);
        Mockito.when(repo.save(p)).thenReturn(new Profissional(1L, "João", "Cabeleireiro", "Seg-Sex 9h-18h", 100.0, null));

        Profissional salvo = service.salvar(p);

        assertNotNull(salvo.getId());
        assertEquals("João", salvo.getNome());
    }
}
