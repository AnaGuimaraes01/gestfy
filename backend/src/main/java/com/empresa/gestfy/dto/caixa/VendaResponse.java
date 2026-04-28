package com.empresa.gestfy.dto.caixa;

/**
 * Response DTO para confirmar uma venda
 * Retorna os detalhes da venda realizada e o troco
 */
public class VendaResponse {
    private Long vendaId;
    private String nomeProduct;
    private Integer quantidade;
    private Double precoUnitario;
    private Double valorTotal;
    private Double valorRecebido;
    private Double troco;
    private String mensagem;

    public VendaResponse(Long vendaId, String nomeProduto, Integer quantidade, 
                         Double precoUnitario, Double valorTotal, 
                         Double valorRecebido, Double troco) {
        this.vendaId = vendaId;
        this.nomeProduct = nomeProduto;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
        this.valorTotal = valorTotal;
        this.valorRecebido = valorRecebido;
        this.troco = troco;
        this.mensagem = "Venda registrada com sucesso!";
    }

    // Getters e Setters
    public Long getVendaId() {
        return vendaId;
    }

    public String getNomeProduct() {
        return nomeProduct;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public Double getPrecoUnitario() {
        return precoUnitario;
    }

    public Double getValorTotal() {
        return valorTotal;
    }

    public Double getValorRecebido() {
        return valorRecebido;
    }

    public Double getTroco() {
        return troco;
    }

    public String getMensagem() {
        return mensagem;
    }
}
