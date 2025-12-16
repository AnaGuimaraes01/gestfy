package com.empresa.gestfy.dto.caixa;

import java.time.LocalDate;

public class CaixaDTO {
    private Long id;
    private Double saldo;
    private String descricao;
    private LocalDate data;

    public CaixaDTO(Long id, Double saldo, String descricao, LocalDate data) {
        this.id = id;
        this.saldo = saldo;
        this.descricao = descricao;
        this.data = data;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getSaldo() {
        return saldo;
    }

    public void setSaldo(Double saldo) {
        this.saldo = saldo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }}
