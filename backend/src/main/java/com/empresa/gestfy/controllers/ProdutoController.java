package com.empresa.gestfy.controllers;

import com.empresa.gestfy.dto.produto.ProdutoDTO;
import com.empresa.gestfy.dto.produto.ProdutoRequest;
import com.empresa.gestfy.models.Produto;
import com.empresa.gestfy.repositories.ProdutoRepository;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins = "*")

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

    private final ProdutoRepository produtoRepository;

    public ProdutoController(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<ProdutoDTO> criar(@Valid @RequestBody ProdutoRequest request) {

        Produto produto = new Produto(
                request.nome(),
                request.descricao(),
                request.preco(),
                request.urlFoto()
        );

        Produto salvo = produtoRepository.save(produto);
        return ResponseEntity.ok(toDTO(salvo));
    }

    // LIST ALL
    @GetMapping
    public ResponseEntity<List<ProdutoDTO>> listar() {
        List<ProdutoDTO> lista = produtoRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();

        return ResponseEntity.ok(lista);
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<ProdutoDTO> buscarPorId(@PathVariable Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        return ResponseEntity.ok(toDTO(produto));
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<ProdutoDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProdutoRequest request) {

        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        produto.setNome(request.nome());
        produto.setDescricao(request.descricao());
        produto.setPreco(request.preco());
        produto.setUrlFoto(request.urlFoto());

        Produto atualizado = produtoRepository.save(produto);

        return ResponseEntity.ok(toDTO(atualizado));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (!produtoRepository.existsById(id)) {
            throw new RuntimeException("Produto não encontrado");
        }

        produtoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // converter Model → DTO
    private ProdutoDTO toDTO(Produto produto) {
        return new ProdutoDTO(
                produto.getId(),
                produto.getNome(),
                produto.getDescricao(),
                produto.getPreco(),
                produto.getUrlFoto()
        );
    }
}
