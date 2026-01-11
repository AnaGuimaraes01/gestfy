package com.empresa.gestfy.dto.cliente;

import com.empresa.gestfy.models.Cliente;

public record ClienteDTO(
    Long id,
    String nome,
    String telefone,
    String email,
    String endereco
) {
    public static ClienteDTO fromEntity(Cliente cliente) {
        return new ClienteDTO(
            cliente.getId(),
            cliente.getNome(),
            cliente.getTelefone(),
            cliente.getEmail(),
            cliente.getEndereco()
        );
    }
}
