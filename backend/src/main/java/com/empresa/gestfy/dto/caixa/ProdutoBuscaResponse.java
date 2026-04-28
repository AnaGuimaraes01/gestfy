package com.empresa.gestfy.dto.caixa;

/**
 * Response DTO para exibir produtos encontrados na busca
 * Usada quando o caixa busca um produto pelo nome (busca parcial)
 */
public class ProdutoBuscaResponse {
    private Long id;
    private String nome;
    private Double preco;
    private Integer estoque;

    public ProdutoBuscaResponse(Long id, String nome, Double preco, Integer estoque) {
        this.id = id;
        this.nome = nome;
        this.preco = preco;
        this.estoque = estoque;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public Double getPreco() {
        return preco;
    }

    public Integer getEstoque() {
        return estoque;
    }
}
