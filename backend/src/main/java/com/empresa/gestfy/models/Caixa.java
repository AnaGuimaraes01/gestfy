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


    @Column(name = "tipo")
    private String tipo;

    // Valor inicial do caixa (nunca pode ser null)
    @Column(name = "valor_inicial", nullable = false, columnDefinition = "DOUBLE PRECISION DEFAULT 0")
    private Double valorInicial;

    // Valor final (apenas preenchido no fechamento)
    @Column(name = "valor_final")
    private Double valorFinal;

    // Saldo ou total de vendas
    @Column(name = "saldo")
    private Double saldo;

    @Column(name = "descricao")
    private String descricao;

    @Column(name = "data")
    private LocalDate data;

    // Data/hora de abertura do caixa
    @Column(name = "data_abertura", nullable = false)
    private LocalDateTime dataAbertura;

    // Data/hora de fechamento do caixa
    @Column(name = "data_fechamento")
    private LocalDateTime dataFechamento;

    // Timestamps mantidos por compatibilidade (caso estejam sendo usados no
    // frontend)
    @Column(name = "horario_abertura")
    private LocalDateTime horarioAbertura;

    @Column(name = "horario_fechamento")
    private LocalDateTime horarioFechamento;

    // Status: "ABERTO", "FECHADO"
    @Column(name = "status", nullable = false)
    private String status;

    // Observações adicionais
    @Column(name = "observacoes")
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

    public Double getValorInicial() {
        return valorInicial;
    }

    public void setValorInicial(Double valorInicial) {
        this.valorInicial = valorInicial != null ? valorInicial : 0.0;
    }

    public Double getValorFinal() {
        return valorFinal;
    }

    public void setValorFinal(Double valorFinal) {
        this.valorFinal = valorFinal;
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

    public LocalDateTime getDataAbertura() {
        return dataAbertura;
    }

    public void setDataAbertura(LocalDateTime dataAbertura) {
        this.dataAbertura = dataAbertura;
    }

    public LocalDateTime getDataFechamento() {
        return dataFechamento;
    }

    public void setDataFechamento(LocalDateTime dataFechamento) {
        this.dataFechamento = dataFechamento;
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

    @PrePersist
    public void prePersist() {
        if (this.valorInicial == null) {
            this.valorInicial = 0.0;
        }
        if (this.dataAbertura == null) {
            this.dataAbertura = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = "ABERTO";
        }
    }
}
