package com.empresa.gestfy.dto.produto;

public record ProdutoDTO(
                Long id,
                String nome,
                String descricao,
                Double preco,
                String urlFoto,
                Integer quantidade,
                Long categoriaId,
                Boolean emPromo,
                Double precoPromo,
                Long visualizacoes,
                Long vendas) {
}
