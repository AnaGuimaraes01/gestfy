package com.empresa.gestfy.dto.pedido;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record PedidoRequest(

        @NotNull(message = "ID do cliente é obrigatório") Long clienteId,

        @NotBlank(message = "Forma de pagamento é obrigatória") String formaPagamento,

        @NotEmpty(message = "Pedido deve ter pelo menos um item") List<PedidoItemRequest> itens) {
}
