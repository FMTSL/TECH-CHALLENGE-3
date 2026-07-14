package com.agendamento.unit;

import com.agendamento.application.AvaliacaoService;
import com.agendamento.domain.Avaliacao;
import com.agendamento.infrastructure.AvaliacaoRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class AvaliacaoServiceTest {

    @Test
    void deveSalvarAvaliacao() {
        AvaliacaoRepository repo = Mockito.mock(AvaliacaoRepository.class);
        AvaliacaoService service = new AvaliacaoService(repo);

        Avaliacao a = new Avaliacao(null, 5, "Ótimo atendimento", null, null, null);
        Mockito.when(repo.save(a)).thenReturn(new Avaliacao(1L, 5, "Ótimo atendimento", null, null, null));

        Avaliacao salva = service.salvar(a);

        assertNotNull(salva.getId());
        assertEquals(5, salva.getNota());
    }
}
