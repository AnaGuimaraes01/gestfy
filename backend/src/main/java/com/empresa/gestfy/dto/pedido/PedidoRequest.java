// package com.empresa.gestfy.dto.pedido;

// import jakarta.validation.constraints.NotBlank;
// import jakarta.validation.constraints.NotNull;
// import jakarta.validation.constraints.NotEmpty;
// import java.util.List;

// public record PedidoRequest(

//                 @NotNull(message = "ID do cliente é obrigatório") Long clienteId,

//                 @NotBlank(message = "Forma de pagamento é obrigatória") String formaPagamento,

//                 @NotBlank(message = "Forma de recebimento é obrigatória (RETIRADA ou ENTREGA)") String formaRecebimento,

//                 String endereco,

//                 @NotEmpty(message = "Pedido deve ter pelo menos um item") List<PedidoItemRequest> itens) {
// }
package com.empresa.gestfy.dto.pedido;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record PedidoRequest(

        // CLIENTE (já vem do front)
        @NotBlank String nomeCliente,
        @NotBlank String telefone,
        String email,

        // PEDIDO
        @NotBlank String formaPagamento,
        @NotBlank String formaRecebimento,
        String endereco,

        @NotEmpty List<PedidoItemRequest> itens
) {}
