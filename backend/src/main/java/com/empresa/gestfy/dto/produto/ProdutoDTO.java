package com.empresa.gestfy.dto.produto;

public record ProdutoDTO(
        Long id,
        String nome,
        String descricao,
        Double preco,
        String urlFoto
) {}
