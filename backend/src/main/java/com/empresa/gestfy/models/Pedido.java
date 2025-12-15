package com.empresa.gestfy.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pedido")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomeCliente;
    private String telefone;
    private String formaPagamento;
    private String status;
    private Double total;

    private LocalDateTime data;

    // 🔹 construtor vazio (obrigatório para JPA)
    public Pedido() {
        this.data = LocalDateTime.now();
    }

    // 🔹 construtor usado no controller
    public Pedido(String nomeCliente, String telefone, String formaPagamento, String status, Double total) {
        this.nomeCliente = nomeCliente;
        this.telefone = telefone;
        this.formaPagamento = formaPagamento;
        this.status = status;
        this.total = total;
        this.data = LocalDateTime.now();
    }

    // 🔹 getters e setters
    public Long getId() {
        return id;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getFormaPagamento() {
        return formaPagamento;
    }

    public String getStatus() {
        return status;
    }

    public Double getTotal() {
        return total;
    }

    public LocalDateTime getData() {
        return data;
    }
}
