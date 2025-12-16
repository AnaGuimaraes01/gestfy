package com.empresa.gestfy.dto.estoque;

import java.time.LocalDateTime;

public class EstoqueDTO {
    private Long id;
    private Long produtoId;
    private String tipoMovimento;
    private LocalDateTime dataMovimento;
    private Integer quantidade;

    public EstoqueDTO(Long id, Long produtoId, String tipoMovimento, LocalDateTime dataMovimento, Integer quantidade) {
        this.id = id;
        this.produtoId = produtoId;
        this.tipoMovimento = tipoMovimento;
        this.dataMovimento = dataMovimento;
        this.quantidade = quantidade;
    }

    public Long getId() { return id; }
    public Long getProdutoId() { return produtoId; }
    public String getTipoMovimento() { return tipoMovimento; }
    public LocalDateTime getDataMovimento() { return dataMovimento; }
    public Integer getQuantidade() { return quantidade; }
}
