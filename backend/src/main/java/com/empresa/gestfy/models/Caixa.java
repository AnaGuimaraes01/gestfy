package com.empresa.gestfy.models;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "caixa")
public class Caixa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // TIPO DE MOVIMENTO: "ENTRADA" (pedido) ou "FECHAMENTO" (resumo do dia)
    private String tipo;

    // Valor inicial do caixa (nunca pode ser null)
    // @Column(nullable = false)
    // private Double valorInicial = 0.0;
    @Column(nullable = false, columnDefinition = "DOUBLE PRECISION DEFAULT 0")
    private Double valorInicial;

    private Double saldo;
    private String descricao;
    private LocalDate data;

    // Timestamps para abertura/fechamento
    private LocalDateTime horarioAbertura;
    private LocalDateTime horarioFechamento;

    // Status: "ABERTO", "FECHADO"
    private String status;

    // Observações adicionais
    private String observacoes;

    public Caixa() {
        this.valorInicial = 0.0;
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

    public Double getValorInicial() {
        return valorInicial;
    }

    public void setValorInicial(Double valorInicial) {
        this.valorInicial = valorInicial != null ? valorInicial : 0.0;
    }

    @PrePersist
    public void prePersist() {
        if (this.valorInicial == null) {
            this.valorInicial = 0.0;
        }
    }
}
