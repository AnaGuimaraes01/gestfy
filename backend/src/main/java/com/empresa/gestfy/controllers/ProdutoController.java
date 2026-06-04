package com.empresa.gestfy.controllers;

import com.empresa.gestfy.dto.produto.ProdutoDTO;
import com.empresa.gestfy.dto.produto.ProdutoRequest;
import com.empresa.gestfy.services.ProdutoService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.empresa.gestfy.dto.caixa.ProdutoBuscaResponse;

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
    public ResponseEntity<List<ProdutoBuscaResponse>> buscarPorNome(@RequestParam String nome) {
        // Delegar a busca ao service (busca parcial, case-insensitive)
        List<ProdutoBuscaResponse> resultados = produtoService.buscarPorNome(nome);
        return ResponseEntity.ok(resultados);
    }

    /**
     * Listar produtos por categoria
     * GET /api/produtos/categoria/{categoriaId}
     */
    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<List<ProdutoDTO>> listarPorCategoria(@PathVariable Long categoriaId) {
        List<ProdutoDTO> produtos = produtoService.listarPorCategoria(categoriaId);
        return ResponseEntity.ok(produtos);
    }

    /**
     * Produtos em promoção
     * GET /api/produtos/destaque/promocoes
     */
    @GetMapping("/destaque/promocoes")
    public ResponseEntity<List<ProdutoDTO>> listarPromocoes() {
        List<ProdutoDTO> produtos = produtoService.listarPromocoes();
        return ResponseEntity.ok(produtos);
    }

    /**
     * Produtos mais vendidos
     * GET /api/produtos/destaque/vendidos
     */
    @GetMapping("/destaque/vendidos")
    public ResponseEntity<List<ProdutoDTO>> listarMaisVendidos() {
        List<ProdutoDTO> produtos = produtoService.listarMaisVendidos();
        return ResponseEntity.ok(produtos);
    }

    /**
     * Produtos mais populares (visualizados)
     * GET /api/produtos/destaque/populares
     */
    @GetMapping("/destaque/populares")
    public ResponseEntity<List<ProdutoDTO>> listarMaisPopulares() {
        List<ProdutoDTO> produtos = produtoService.listarMaisPopulares();
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

    /**
     * Incrementar visualizações do produto
     * PUT /api/produtos/{id}/incrementar-visualizacoes
     */
    @PutMapping("/{id}/incrementar-visualizacoes")
    public ResponseEntity<ProdutoDTO> incrementarVisualizacoes(@PathVariable Long id) {
        try {
            ProdutoDTO produto = produtoService.incrementarVisualizacoes(id);
            return ResponseEntity.ok(produto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Incrementar vendas do produto
     * PUT /api/produtos/{id}/incrementar-vendas/{quantidade}
     */
    @PutMapping("/{id}/incrementar-vendas/{quantidade}")
    public ResponseEntity<ProdutoDTO> incrementarVendas(
            @PathVariable Long id,
            @PathVariable Integer quantidade) {
        try {
            ProdutoDTO produto = produtoService.incrementarVendas(id, quantidade);
            return ResponseEntity.ok(produto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
