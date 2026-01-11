package com.empresa.gestfy.controllers;

import com.empresa.gestfy.dto.cliente.ClienteRequest;
import com.empresa.gestfy.dto.cliente.ClienteDTO;
import com.empresa.gestfy.models.Cliente;
import com.empresa.gestfy.repositories.ClienteRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteRepository clienteRepository;

    public ClienteController(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    // =========================
    // CRIAR CLIENTE
    // =========================
    @PostMapping
    public ResponseEntity<ClienteDTO> criarCliente(@RequestBody @Valid ClienteRequest request) {
        Cliente cliente = new Cliente();
        cliente.setNome(request.nome());
        cliente.setTelefone(request.telefone());
        cliente.setEmail(request.email());
        cliente.setEndereco(request.endereco());

        cliente = clienteRepository.save(cliente);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ClienteDTO(cliente.getId(), cliente.getNome(), cliente.getTelefone(), cliente.getEmail(), cliente.getEndereco()));
    }

    // =========================
    // LISTAR TODOS OS CLIENTES
    // =========================
    @GetMapping
    public ResponseEntity<List<ClienteDTO>> listarClientes() {
        List<ClienteDTO> clientes = clienteRepository.findAll()
                .stream(), c.getEndereco()
                .map(c -> new ClienteDTO(c.getId(), c.getNome(), c.getTelefone(), c.getEmail()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(clientes);
    }

    // =========================
    // BUSCAR CLIENTE POR ID
    // =========================
    @GetMapping("/{id}")
    public ResponseEntity<ClienteDTO> buscarPorId(@PathVariable Long id) {
        return clienteRepository.findById(id)
                .map(c -> new ClienteDTO(c.getId(), c.getNome(), c.getTelefone(), c.getEmail(), c.getEndereco()))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // =========================
    // ATUALIZAR CLIENTE
    // =========================
    @PutMapping("/{id}")
    public ResponseEntity<ClienteDTO> atualizarCliente(
            @PathVariable Long id,
            @RequestBody @Valid ClienteRequest request) {

        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        cliente.setNome(request.nome());
        cliente.setTelefone(request.telefone());
        cliente.setEmail(request.email());
        cliente.setEndereco(request.endereco());

        cliente = clienteRepository.save(cliente);

        return ResponseEntity
                .ok(new ClienteDTO(cliente.getId(), cliente.getNome(), cliente.getTelefone(), cliente.getEmail(), cliente.getEndereco()));
    }

    // =========================
    // REMOVER CLIENTE
    // =========================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removerCliente(@PathVariable Long id) {
        if (!clienteRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        clienteRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
