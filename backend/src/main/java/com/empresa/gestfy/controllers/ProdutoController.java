package com.empresa.gestfy.controllers;

import com.empresa.gestfy.dto.produto.ProdutoDTO;
import com.empresa.gestfy.dto.produto.ProdutoRequest;
import com.empresa.gestfy.models.Produto;
import com.empresa.gestfy.repositories.ProdutoRepository;
import com.empresa.gestfy.models.Estoque;
import com.empresa.gestfy.repositories.EstoqueRepository;
import java.time.LocalDateTime;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins = "*")

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

    private final ProdutoRepository produtoRepository;
    private final EstoqueRepository estoqueRepository;

    public ProdutoController(ProdutoRepository produtoRepository, EstoqueRepository estoqueRepository) {
        this.produtoRepository = produtoRepository;
        this.estoqueRepository = estoqueRepository;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<ProdutoDTO> criar(@Valid @RequestBody ProdutoRequest request) {
        Produto produto = new Produto(
                request.nome(),
                request.descricao(),
                request.preco(),
                request.urlFoto(),
                request.quantidade()
        );
        Produto salvo = produtoRepository.save(produto);

        // Cria movimentação ENTRADA no estoque automaticamente
        Estoque entrada = new Estoque();
        entrada.setProdutoId(salvo.getId());
        entrada.setTipoMovimento("ENTRADA");
        entrada.setQuantidade(salvo.getQuantidade());
        entrada.setDataMovimento(LocalDateTime.now());
        estoqueRepository.save(entrada);

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

    // BUSCAR POR NOME
    @GetMapping("/buscar")
    public ResponseEntity<List<ProdutoDTO>> buscarPorNome(@RequestParam String nome) {
        List<ProdutoDTO> lista = produtoRepository.findAll()
                .stream()
                .filter(p -> p.getNome().toLowerCase().contains(nome.toLowerCase()))
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
        produto.setQuantidade(request.quantidade());
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
                produto.getUrlFoto(),
                produto.getQuantidade()
        );
    }
}
