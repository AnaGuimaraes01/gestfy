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

    // Relacionamento com Cliente
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    private String formaPagamento;
    private String formaRecebimento; // RETIRADA ou ENTREGA
    private String endereco;
    private String status;

    @Column(nullable = false)
    private Double total = 0.0;

    // ⚠️ NÃO inicializar aqui para evitar falha no DDL
    @Column(nullable = false)
    private LocalDateTime data;

    // Itens do pedido
    @OneToMany(
            mappedBy = "pedido",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @JsonManagedReference
    private List<PedidoItem> itens = new ArrayList<>();

    // Construtor padrão
    public Pedido() {
        this.data = LocalDateTime.now();
    }

    // ======================
    // GETTERS E SETTERS
    // ======================

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

    public String getFormaRecebimento() {
        return formaRecebimento;
    }

    public void setFormaRecebimento(String formaRecebimento) {
        this.formaRecebimento = formaRecebimento;
    }

    public String getEndereco() {
 return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getTotal() {
        return itens.stream()
                .mapToDouble(item ->
                        item.getPrecoUnitario() * item.getQuantidade()
                )
                .sum();
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

    // ======================
    // MÉTODO AUXILIAR
    // ======================

    public void addItem(PedidoItem item) {
        itens.add(item);
        item.setPedido(this);
    }
}