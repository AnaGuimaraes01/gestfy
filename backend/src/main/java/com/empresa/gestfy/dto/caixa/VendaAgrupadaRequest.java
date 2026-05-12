package com.empresa.gestfy.dto.caixa;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * Request DTO para registrar uma venda agrupada com múltiplos itens
 * Contém: lista de itens e valor recebido em dinheiro
 */
public record VendaAgrupadaRequest(
        @NotEmpty(message = "A venda deve ter pelo menos um item")
        List<ItemVendaRequest> itens,

        @PositiveOrZero(message = "Valor recebido não pode ser negativo")
        Double valorRecebido) {
}
