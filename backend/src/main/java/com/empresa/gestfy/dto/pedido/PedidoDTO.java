package com.empresa.gestfy.dto.pedido;

import java.time.LocalDateTime;

public record PedidoDTO(
        Long id,
        String nomeCliente,
        String telefone,
        String formaPagamento,
        String status,
        Double total,
        LocalDateTime data
) {}
