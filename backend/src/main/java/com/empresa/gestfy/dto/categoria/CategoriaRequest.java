package com.empresa.gestfy.dto.categoria;

import jakarta.validation.constraints.NotBlank;

public record CategoriaRequest(

        @NotBlank(message = "O nome da categoria é obrigatório") String nome

) {
}
