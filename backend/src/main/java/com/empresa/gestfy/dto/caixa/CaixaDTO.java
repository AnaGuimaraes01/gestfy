package com.empresa.gestfy.dto.caixa;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CaixaDTO {
    private Long id;
    private String tipo;
    private Double saldo;
    private String descricao;
    private LocalDate data;
    private LocalDateTime horarioAbertura;
    private LocalDateTime horarioFechamento;
    private String status;
    private String observacoes;

    public CaixaDTO(Long id, String tipo, Double saldo, String descricao, LocalDate data,
            LocalDateTime horarioAbertura, LocalDateTime horarioFechamento,
            String status, String observacoes) {
        this.id = id;
        this.tipo = tipo;
        this.saldo = saldo;
        this.descricao = descricao;
        this.data = data;
        this.horarioAbertura = horarioAbertura;
        this.horarioFechamento = horarioFechamento;
        this.status = status;
        this.observacoes = observacoes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
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
    }

    public LocalDateTime getHorarioAbertura() {
        return horarioAbertura;
    }

    public void setHorarioAbertura(LocalDateTime horarioAbertura) {
        this.horarioAbertura = horarioAbertura;
    }

    public LocalDateTime getHorarioFechamento() {
        return horarioFechamento;
    }

    public void setHorarioFechamento(LocalDateTime horarioFechamento) {
        this.horarioFechamento = horarioFechamento;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}
