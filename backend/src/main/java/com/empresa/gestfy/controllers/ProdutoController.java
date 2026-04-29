package com.empresa.gestfy.controllers;

import com.empresa.gestfy.dto.produto.ProdutoDTO;
import com.empresa.gestfy.dto.produto.ProdutoRequest;
import com.empresa.gestfy.services.ProdutoService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ProdutoController
 * Responsável apenas por:
 * - Receber requisições HTTP
 * - Delegar para ProdutoService
 * - Retornar respostas HTTP
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    /**
     * Criar novo produto
     * POST /api/produtos
     */
    @PostMapping
    public ResponseEntity<ProdutoDTO> criar(@Valid @RequestBody ProdutoRequest request) {
        ProdutoDTO produto = produtoService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(produto);
    }

    /**
     * Listar todos os produtos
     * GET /api/produtos
     */
    @GetMapping
    public ResponseEntity<List<ProdutoDTO>> listar() {
        return ResponseEntity.ok(produtoService.listar());
    }

    /**
     * Buscar produtos pelo nome
     * GET /api/produtos/buscar?nome=sorvete
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<ProdutoDTO>> buscarPorNome(@RequestParam String nome) {
        // Nota: buscarPorNome do ProdutoService retorna ProdutoBuscaResponse
        // Para essa busca avançada, precisamos listar todos e filtrar
        List<ProdutoDTO> produtos = produtoService.listar()
                .stream()
                .filter(p -> p.nome().toLowerCase().contains(nome.toLowerCase()))
                .toList();

        return ResponseEntity.ok(produtos);
    }

    /**
     * Buscar produto por ID
     * GET /api/produtos/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProdutoDTO> buscarPorId(@PathVariable Long id) {
        return produtoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Atualizar produto
     * PUT /api/produtos/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProdutoDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProdutoRequest request) {
        try {
            ProdutoDTO produto = produtoService.atualizar(id, request);
            return ResponseEntity.ok(produto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Remover produto
     * DELETE /api/produtos/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        try {
            produtoService.remover(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
