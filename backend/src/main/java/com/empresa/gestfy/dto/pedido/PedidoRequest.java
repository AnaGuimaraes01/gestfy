package com.empresa.gestfy.dto.pedido;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record PedidoRequest(

    @NotNull
    Long clienteId,

    @NotNull
    String formaPagamento,

    @NotNull
    List<PedidoItemRequest> itens
) {}
