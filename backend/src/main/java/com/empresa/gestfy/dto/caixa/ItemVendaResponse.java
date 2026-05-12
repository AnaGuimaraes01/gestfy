package com.empresa.gestfy.dto.caixa;

/**
 * Response DTO para um item individual da venda agrupada
 */
public class ItemVendaResponse {
    private Long produtoId;
    private String nomeProduto;
    private Integer quantidade;
    private Double precoUnitario;
    private Double subtotal;

    public ItemVendaResponse(Long produtoId, String nomeProduto, Integer quantidade, 
                           Double precoUnitario, Double subtotal) {
        this.produtoId = produtoId;
        this.nomeProduto = nomeProduto;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
        this.subtotal = subtotal;
    }

    // Getters
    public Long getProdutoId() {
        return produtoId;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public Double getPrecoUnitario() {
        return precoUnitario;
    }

    public Double getSubtotal() {
        return subtotal;
    }
}
