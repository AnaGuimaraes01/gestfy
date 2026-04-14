package com.empresa.gestfy.dto.caixa;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record CaixaRequest(
        @NotNull(message = "Tipo de movimento é obrigatório (ENTRADA ou FECHAMENTO)") String tipo,

        Double saldo,

        @NotBlank(message = "Descrição é obrigatória") String descricao,

        LocalDate data,

        String observacoes) {
}
