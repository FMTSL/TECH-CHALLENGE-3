package com.agendamento.unit;

import com.agendamento.application.EstabelecimentoService;
import com.agendamento.domain.Estabelecimento;
import com.agendamento.infrastructure.EstabelecimentoRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EstabelecimentoServiceTest {

    @Test
    void deveSalvarEstabelecimento() {
        EstabelecimentoRepository repo = Mockito.mock(EstabelecimentoRepository.class);
        EstabelecimentoService service = new EstabelecimentoService(repo);

        Estabelecimento e = new Estabelecimento(null, "Salão Beleza", "Rua A", "Corte", "9-18h", "foto.png");
        Mockito.when(repo.save(e)).thenReturn(new Estabelecimento(1L, "Salão Beleza", "Rua A", "Corte", "9-18h", "foto.png"));

        Estabelecimento salvo = service.salvar(e);

        assertNotNull(salvo.getId());
        assertEquals("Salão Beleza", salvo.getNome());
    }
}
