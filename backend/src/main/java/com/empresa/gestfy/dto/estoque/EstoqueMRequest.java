package com.empresa.gestfy.dto.estoque;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record EstoqueMRequest(
    @NotNull(message = "ID do produto é obrigatório")
    Long produtoId,
    
    @NotBlank(message = "Tipo de movimento é obrigatório (ENTRADA/SAIDA)")
    String tipoMovimento,
    
    @NotNull(message = "Quantidade é obrigatória")
    @Positive(message = "Quantidade deve ser maior que 0")
    Integer quantidade
) {}
