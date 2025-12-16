package com.empresa.gestfy.dto.caixa;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CaixaRequest(
    @NotNull(message = "Saldo é obrigatório")
    Double saldo,

    @NotBlank(message = "Descrição é obrigatória")
    String descricao
) {}
