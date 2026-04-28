package com.empresa.gestfy.dto.caixa;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * Request DTO para registrar uma venda no caixa
 * Contém: produto selecionado, quantidade comprada e valor recebido em dinheiro
 */
public record VendaRequest(
        @NotNull(message = "ID do produto é obrigatório")
        Long produtoId,

        @Positive(message = "Quantidade deve ser maior que zero")
        Integer quantidade,

        @PositiveOrZero(message = "Valor recebido não pode ser negativo")
        Double valorRecebido
) {}
