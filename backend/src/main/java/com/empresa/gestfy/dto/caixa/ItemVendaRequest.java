package com.empresa.gestfy.dto.caixa;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Request DTO para um item individual da venda agrupada
 * Contém: produto, quantidade
 */
public record ItemVendaRequest(
        @NotNull(message = "ID do produto é obrigatório") Long produtoId,

        @Positive(message = "Quantidade deve ser maior que zero") Integer quantidade) {
}
