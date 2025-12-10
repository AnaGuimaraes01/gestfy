package com.empresa.gestfy.controllers;

import com.empresa.gestfy.models.Estoque;
import com.empresa.gestfy.models.Produto;
import com.empresa.gestfy.repositories.EstoqueRepository;
import com.empresa.gestfy.repositories.ProdutoRepository;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produtos")
@CrossOrigin("*")
public class ProdutoController {

    private final ProdutoRepository produtoRepository;
    private final EstoqueRepository estoqueRepository;

    public ProdutoController(
            ProdutoRepository produtoRepository,
            EstoqueRepository estoqueRepository
    ) {
        this.produtoRepository = produtoRepository;
        this.estoqueRepository = estoqueRepository;
    }

    // ✅ LISTAR PRODUTOS
    @GetMapping
    public List<Produto> listar() {
        return produtoRepository.findAll();
    }

    // ✅ BUSCAR POR ID
    @GetMapping("/{id}")
    public Produto buscar(@PathVariable Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
    }

    // ✅ CRIAR PRODUTO + ESTOQUE INICIAL
    @PostMapping
    public Produto criar(@RequestBody Produto produto) {

        Produto salvo = produtoRepository.save(produto);

        Estoque estoque = new Estoque();
        estoque.setProdutoId(salvo.getId());
        estoque.setQuantidade(0);
        estoqueRepository.save(estoque);

        return salvo;
    }

    // ✅ ATUALIZAR PRODUTO
    @PutMapping("/{id}")
    public Produto atualizar(@PathVariable Long id, @RequestBody Produto dados) {

        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        produto.setNome(dados.getNome());
        produto.setDescricao(dados.getDescricao());
        produto.setPreco(dados.getPreco());

        return produtoRepository.save(produto);
    }

    // ✅ DELETAR PRODUTO + ESTOQUE
    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        estoqueRepository.deleteById(id);
        produtoRepository.deleteById(id);
    }
}