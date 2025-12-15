package com.empresa.gestfy.dto.pedido;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record PedidoRequest(
    @NotBlank
    String nomeCliente,

    @NotBlank
    String telefone,

    @NotBlank
    String formaPagamento,

    @NotBlank
    String status,

    @NotNull
    @Positive
    Double total,

    @NotNull
    List<PedidoItemRequest> itens   // <<< adiciona esta lista aqui
) {}
