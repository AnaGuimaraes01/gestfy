package com.empresa.gestfy.dto.caixa;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ItemVendaRequest(
        @NotNull(message = "ID do produto é obrigatório") Long produtoId,

        @Positive(message = "Quantidade deve ser maior que zero") Integer quantidade) {
}
