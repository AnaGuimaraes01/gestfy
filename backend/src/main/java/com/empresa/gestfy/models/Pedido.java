package com.empresa.gestfy.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedido")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔹 Relacionamento com Cliente
    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    private String formaPagamento;
    private String status;
    private Double total;
    private LocalDateTime data;

    // 🔹 Relacionamento com itens do pedido
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PedidoItem> itens = new ArrayList<>();

    // 🔹 Construtor vazio obrigatório para JPA
    public Pedido() {
        this.data = LocalDateTime.now();
    }

    // 🔹 Construtor com parâmetros (opcional)
    public Pedido(Cliente cliente, String formaPagamento, String status, Double total) {
        this.cliente = cliente;
        this.formaPagamento = formaPagamento;
        this.status = status;
        this.total = total;
        this.data = LocalDateTime.now();
    }

    // ====================
    // Getters e Setters
    // ====================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public String getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(String formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public List<PedidoItem> getItens() {
        return itens;
    }

    public void setItens(List<PedidoItem> itens) {
        this.itens = itens;
        // garante que cada item sabe que pertence a este pedido
        for (PedidoItem item : itens) {
            item.setPedido(this);
        }
    }

    // ====================
    // Métodos auxiliares
    // ====================

    public void addItem(PedidoItem item) {
        item.setPedido(this);
        itens.add(item);
    }

    public void removeItem(PedidoItem item) {
        itens.remove(item);
        item.setPedido(null);
    }
}
