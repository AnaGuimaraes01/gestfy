package com.empresa.gestfy.dto.pedido;

public class PedidoItemRequest {

    private Long idProduto; // alterado de produtoId para idProduto
    private Integer quantidade;

    public PedidoItemRequest() {}

    public Long getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(Long idProduto) {
        this.idProduto = idProduto;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }
}
