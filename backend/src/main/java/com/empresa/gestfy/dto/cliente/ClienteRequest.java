package com.empresa.gestfy.dto.cliente;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ClienteRequest(

    @NotBlank(message = "O nome do cliente é obrigatório")
    String nome,

    @NotBlank(message = "O telefone do cliente é obrigatório")
    String telefone,

    @Email(message = "E-mail inválido")
    String email
) {}
