package com.empresa.gestfy.dto.pedido;

import java.time.LocalDateTime;
import java.util.List;

public record PedidoDTO(
        Long id,
        String nomeCliente,
        String telefone,
        String endereco,
        String formaPagamento,
        String formaRecebimento,
        String status,
        Double total,
        LocalDateTime data,
        List<PedidoItemDTO> itens) {
}
