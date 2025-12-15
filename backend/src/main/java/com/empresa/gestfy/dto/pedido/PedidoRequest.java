package com.empresa.gestfy.dto.pedido;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record PedidoRequest(

    @NotBlank
    String nomeCliente,

    @NotBlank
    String telefone,

    @NotBlank
    String formaPagamento,

    @NotNull
    List<PedidoItemRequest> itens
) {}
