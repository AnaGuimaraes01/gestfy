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

/* ProdutoController responsável por:
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

    @PostMapping
    public ResponseEntity<ProdutoDTO> criar(@Valid @RequestBody ProdutoRequest request) {
        ProdutoDTO produto = produtoService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(produto);
    }

    @GetMapping
    public ResponseEntity<List<ProdutoDTO>> listar() {
        return ResponseEntity.ok(produtoService.listar());
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<ProdutoBuscaResponse>> buscarPorNome(@RequestParam String nome) {
        List<ProdutoBuscaResponse> resultados = produtoService.buscarPorNome(nome);
        return ResponseEntity.ok(resultados);
    }

    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<List<ProdutoDTO>> listarPorCategoria(@PathVariable Long categoriaId) {
        List<ProdutoDTO> produtos = produtoService.listarPorCategoria(categoriaId);
        return ResponseEntity.ok(produtos);
    }

    @GetMapping("/destaque/promocoes")
    public ResponseEntity<List<ProdutoDTO>> listarPromocoes() {
        List<ProdutoDTO> produtos = produtoService.listarPromocoes();
        return ResponseEntity.ok(produtos);
    }

    @GetMapping("/destaque/vendidos")
    public ResponseEntity<List<ProdutoDTO>> listarMaisVendidos() {
        List<ProdutoDTO> produtos = produtoService.listarMaisVendidos();
        return ResponseEntity.ok(produtos);
    }

    @GetMapping("/destaque/populares")
    public ResponseEntity<List<ProdutoDTO>> listarMaisPopulares() {
        List<ProdutoDTO> produtos = produtoService.listarMaisPopulares();
        return ResponseEntity.ok(produtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoDTO> buscarPorId(@PathVariable Long id) {
        return produtoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        try {
            produtoService.remover(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/incrementar-visualizacoes")
    public ResponseEntity<ProdutoDTO> incrementarVisualizacoes(@PathVariable Long id) {
        try {
            ProdutoDTO produto = produtoService.incrementarVisualizacoes(id);
            return ResponseEntity.ok(produto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

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
