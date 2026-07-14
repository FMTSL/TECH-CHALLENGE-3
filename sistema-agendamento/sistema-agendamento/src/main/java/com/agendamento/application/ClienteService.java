package com.agendamento.application;

import com.agendamento.domain.Cliente;
import com.agendamento.infrastructure.ClienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {
    private final ClienteRepository repo;

    public ClienteService(ClienteRepository repo) {
        this.repo = repo;
    }

    public Cliente criar(Cliente cliente) {
        return repo.save(cliente);
    }

    public List<Cliente> listarTodos() {
        return repo.findAll();
    }

    public Optional<Cliente> buscarPorId(Long id) {
        return repo.findById(id);
    }
}
