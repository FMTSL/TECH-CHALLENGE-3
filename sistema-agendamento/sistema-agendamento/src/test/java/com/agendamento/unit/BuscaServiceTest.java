package com.agendamento.unit;

import com.agendamento.application.BuscaService;
import com.agendamento.domain.Profissional;
import com.agendamento.infrastructure.EstabelecimentoRepository;
import com.agendamento.infrastructure.ProfissionalRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BuscaServiceTest {

    @Test
    void deveBuscarProfissionaisPorEspecialidade() {
        ProfissionalRepository repo = Mockito.mock(ProfissionalRepository.class);
        EstabelecimentoRepository estRepo = Mockito.mock(EstabelecimentoRepository.class);
        BuscaService service = new BuscaService(estRepo, repo);

        Profissional p = new Profissional(1L, "Maria", "Manicure", "Seg-Sex", 50.0, null);
        Mockito.when(repo.findByEspecialidadeContainingIgnoreCase("Manicure"))
                .thenReturn(List.of(p));

        List<Profissional> resultado = service.buscarProfissionaisPorEspecialidade("Manicure");

        assertEquals(1, resultado.size());
        assertEquals("Maria", resultado.get(0).getNome());
    }
}
