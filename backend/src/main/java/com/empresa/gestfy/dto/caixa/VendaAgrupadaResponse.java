package com.empresa.gestfy.dto.caixa;

import java.util.List;

/**
 * Response DTO para confirmar uma venda agrupada
 * Retorna os detalhes de todos os itens, total, troco e ID do pedido
 */
public class VendaAgrupadaResponse {
    private Long pedidoId;
    private List<ItemVendaResponse> itens;
    private Double valorTotal;
    private Double valorRecebido;
    private Double troco;
    private Integer totalItens;
    private String mensagem;

    public VendaAgrupadaResponse(Long pedidoId, List<ItemVendaResponse> itens, Double valorTotal, 
                                  Double valorRecebido, Double troco, Integer totalItens) {
        this.pedidoId = pedidoId;
        this.itens = itens;
        this.valorTotal = valorTotal;
        this.valorRecebido = valorRecebido;
        this.troco = troco;
        this.totalItens = totalItens;
        this.mensagem = "Venda agrupada registrada com sucesso!";
    }

    // Getters
    public Long getPedidoId() {
        return pedidoId;
    }

    public List<ItemVendaResponse> getItens() {
        return itens;
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

    public Integer getTotalItens() {
        return totalItens;
    }

    public String getMensagem() {
        return mensagem;
    }
}
