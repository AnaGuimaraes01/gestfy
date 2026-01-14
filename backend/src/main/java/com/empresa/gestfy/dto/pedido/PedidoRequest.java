package com.empresa.gestfy.dto.pedido;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import java.util.List;

public record PedidoRequest(

        // CLIENTE - Dados fornecidos no momento do pedido
        @NotBlank(message = "Nome do cliente é obrigatório") String nomeCliente,

        @NotBlank(message = "Telefone do cliente é obrigatório") 
        @Pattern(regexp = "^[0-9\\-\\(\\)\\s]+$", message = "Telefone inválido") 
        String telefoneCliente,

        // PEDIDO
        @NotBlank(message = "Forma de pagamento é obrigatória") String formaPagamento,

        @NotBlank(message = "Forma de recebimento é obrigatória (RETIRADA ou ENTREGA)") String formaRecebimento,

        String endereco,

        @NotEmpty(message = "Pedido deve ter pelo menos um item") List<PedidoItemRequest> itens
) {}
