package com.empresa.gestfy.services;

import com.empresa.gestfy.dto.cliente.ClienteDTO;
import com.empresa.gestfy.dto.cliente.ClienteRequest;
import com.empresa.gestfy.models.Cliente;
import com.empresa.gestfy.repositories.ClienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

//cliente service: operações crud de clientes
@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    //criar novo cliente
    public ClienteDTO criar(ClienteRequest request) {
        Cliente cliente = new Cliente();
        cliente.setNome(request.nome());
        cliente.setTelefone(request.telefone());
        cliente.setEmail(request.email());
        cliente.setEndereco(request.endereco());

        cliente = clienteRepository.save(cliente);
        return toDTO(cliente);
    }

    //listar clientes
    public List<ClienteDTO> listar() {
        return clienteRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

   //buscar cliente por id
    public Optional<ClienteDTO> buscarPorId(Long id) {
        return clienteRepository.findById(id)
                .map(this::toDTO);
    }

    //atualizar cliente
    public ClienteDTO atualizar(Long id, ClienteRequest request) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        cliente.setNome(request.nome());
        cliente.setTelefone(request.telefone());
        cliente.setEmail(request.email());
        cliente.setEndereco(request.endereco());

        cliente = clienteRepository.save(cliente);
        return toDTO(cliente);
    }

    public void remover(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new RuntimeException("Cliente não encontrado");
        }
        clienteRepository.deleteById(id);
    }

//converter modelo para dto
    private ClienteDTO toDTO(Cliente cliente) {
        return new ClienteDTO(
                cliente.getId(),
                cliente.getNome(),
                cliente.getTelefone(),
                cliente.getEmail(),
                cliente.getEndereco());
    }
}
