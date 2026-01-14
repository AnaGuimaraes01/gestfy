

package com.empresa.gestfy.dto.cliente;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ClienteRequest(

    @NotBlank(message = "O nome do cliente é obrigatório")
    String nome,

    @NotBlank(message = "O telefone do cliente é obrigatório")
    String telefone,

    @NotBlank(message = "O e-mail do cliente é obrigatório")
    @Email(message = "E-mail inválido")
    String email,

    String endereco // ALTERAÇÃO: Endereço pode ser informado depois no pedido
) {}
