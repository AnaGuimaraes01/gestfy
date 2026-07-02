package com.empresa.gestfy.dto.relatorio;

public class ProdutoMaisVendidoDTO {
    private String nome;
    private Integer quantidadeVendida;
    private Double valorTotal;

    public ProdutoMaisVendidoDTO() {
    }

    public ProdutoMaisVendidoDTO(String nome, Integer quantidadeVendida, Double valorTotal) {
        this.nome = nome;
        this.quantidadeVendida = quantidadeVendida;
        this.valorTotal = valorTotal;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getQuantidadeVendida() {
        return quantidadeVendida;
    }

    public void setQuantidadeVendida(Integer quantidadeVendida) {
        this.quantidadeVendida = quantidadeVendida;
    }

    public Double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(Double valorTotal) {
        this.valorTotal = valorTotal;
    }
}
