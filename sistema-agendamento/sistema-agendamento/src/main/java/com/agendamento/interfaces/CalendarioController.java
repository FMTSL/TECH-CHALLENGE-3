package com.agendamento.interfaces;

import com.agendamento.application.CalendarioService;
import com.agendamento.domain.Agendamento;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/calendario")
public class CalendarioController {
    private final CalendarioService service;

    public CalendarioController(CalendarioService service) {
        this.service = service;
    }

    @PostMapping("/google")
    public ResponseEntity<String> sincronizarGoogle(@RequestBody Agendamento agendamento) {
        service.sincronizarComGoogle(agendamento);
        return ResponseEntity.ok("Agendamento sincronizado com Google Calendar!");
    }
}
