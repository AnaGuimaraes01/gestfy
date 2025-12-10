package com.empresa.gestfy.dto.pedido;

public class PedidoItemRequest {

    private Long produtoId;
    private Integer quantidade;

    public PedidoItemRequest() {}

    public Long getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(Long produtoId) {
        this.produtoId = produtoId;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

}
