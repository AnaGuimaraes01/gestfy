package com.empresa.gestfy.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    private String formaPagamento;
    private String status;

    @Column(nullable = false, columnDefinition = "DOUBLE DEFAULT 0.0")
    private Double total = 0.0;

    private LocalDateTime data;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<PedidoItem> itens = new ArrayList<>();

    public Pedido() {
        this.data = LocalDateTime.now();
    }

    // =======================
    // GETTERS E SETTERS
    // =======================
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
        // Se total for null ou zero, calcula a partir dos itens
        if (total == null || total <= 0) {
            return itens.stream()
                    .mapToDouble(item -> (item.getPrecoUnitario() != null ? item.getPrecoUnitario() : 0.0) *
                            (item.getQuantidade() != null ? item.getQuantidade() : 0))
                    .sum();
        }
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
        this.itens.clear();
        if (itens != null) {
            for (PedidoItem item : itens) {
                addItem(item);
            }
        }
    }

    // =======================
    // MÉTODO PARA ADICIONAR ITENS
    // =======================
    public void addItem(PedidoItem item) {
        itens.add(item);
        item.setPedido(this);
    }
}

// package com.empresa.gestfy.models;

// import com.fasterxml.jackson.annotation.JsonManagedReference;
// import jakarta.persistence.*;
// import java.time.LocalDateTime;
// import java.util.ArrayList;
// import java.util.List;

// @Entity
// @Table(name = "pedido")
// public class Pedido {

// @Id
// @GeneratedValue(strategy = GenerationType.IDENTITY)
// private Long id;

// @ManyToOne
// @JoinColumn(name = "cliente_id", nullable = false)
// private Cliente cliente;

// private String formaPagamento;
// private String status;
// private Double total;
// private LocalDateTime data;

// @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval =
// true)
// @JsonManagedReference
// private List<PedidoItem> itens = new ArrayList<>();

// public Pedido() {
// this.data = LocalDateTime.now();
// }

// // getters e setters
// public Long getId() { return id; }
// public void setId(Long id) { this.id = id; }

// public Cliente getCliente() { return cliente; }
// public void setCliente(Cliente cliente) { this.cliente = cliente; }

// public String getFormaPagamento() { return formaPagamento; }
// public void setFormaPagamento(String formaPagamento) { this.formaPagamento =
// formaPagamento; }

// public String getStatus() { return status; }
// public void setStatus(String status) { this.status = status; }

// public Double getTotal() { return total; }
// public void setTotal(Double total) { this.total = total; }

// public LocalDateTime getData() { return data; }
// public void setData(LocalDateTime data) { this.data = data; }

// public List<PedidoItem> getItens() { return itens; }
// public void setItens(List<PedidoItem> itens) { this.itens = itens; }
// }
