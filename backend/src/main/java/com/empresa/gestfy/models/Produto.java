package com.empresa.gestfy.models;


import jakarta.persistence.*;

@Entity
@Table(name = "produto")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String descricao;
    private Double preco;
    private String urlFoto;
    private Integer quantidade;

    public Produto() {
    }
        public Produto(String nome, String descricao, Double preco, String urlFoto, Integer quantidade) {
                this.nome = nome;
                this.descricao = descricao;
                this.preco = preco;
                this.urlFoto = urlFoto;
                this.quantidade = quantidade;
        }
        public Integer getQuantidade() {
            return quantidade;
        }
        public void setQuantidade(Integer quantidade) {
            this.quantidade = quantidade;
        }
      public Long getId() {
          return id;
      }
      public void setId(Long id) {
          this.id = id;
      }
      public String getNome() {
          return nome;
      }
      public void setNome(String nome) {
          this.nome = nome;
      }
      public String getDescricao() {
          return descricao;
      }
      public void setDescricao(String descricao) {
          this.descricao = descricao;
      }
      public Double getPreco() {
          return preco;
      }
      public void setPreco(Double preco) {
          this.preco = preco;
      }
      public String getUrlFoto() {
          return urlFoto;
      }
      public void setUrlFoto(String urlFoto) {
          this.urlFoto = urlFoto;
      }
}