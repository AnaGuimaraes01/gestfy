package com.empresa.gestfy.dto.pedido;

import java.util.List;

public class PedidoRequest {

    private String nomeCliente;
    private String telefone;
    private String formaRecebimento;
    private List<PedidoItemRequest> itens;

    public PedidoRequest() {}

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getFormaRecebimento() {
        return formaRecebimento;
    }

    public void setFormaRecebimento(String formaRecebimento) {
        this.formaRecebimento = formaRecebimento;
    }

    public List<PedidoItemRequest> getItens() {
        return itens;
    }

    public void setItens(List<PedidoItemRequest> itens) {
        this.itens = itens;
    }

}
