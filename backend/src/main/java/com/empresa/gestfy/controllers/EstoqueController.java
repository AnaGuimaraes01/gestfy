package com.empresa.gestfy.controllers;

import com.empresa.gestfy.dto.estoque.EstoqueDTO;
import com.empresa.gestfy.dto.estoque.EstoqueRequest;
import com.empresa.gestfy.services.EstoqueService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/* EstoqueController responsável por:
 * - Receber requisições HTTP
 * - Delegar para EstoqueService
 * - Retornar respostas HTTP
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/estoque")
public class EstoqueController {

    private final EstoqueService estoqueService;

    public EstoqueController(EstoqueService estoqueService) {
        this.estoqueService = estoqueService;
    }

    @PostMapping
    public ResponseEntity<EstoqueDTO> criar(@Valid @RequestBody EstoqueRequest request) {
        EstoqueDTO estoque = estoqueService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(estoque);
    }

    @GetMapping
    public ResponseEntity<List<EstoqueDTO>> listar() {
        return ResponseEntity.ok(estoqueService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstoqueDTO> buscarPorId(@PathVariable Long id) {
        return estoqueService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/filtro/tipo")
    public ResponseEntity<List<EstoqueDTO>> filtrarPorTipo(@RequestParam String tipo) {
        return ResponseEntity.ok(estoqueService.filtrarPorTipo(tipo));
    }

    @GetMapping("/filtro/data")
    public ResponseEntity<List<EstoqueDTO>> filtrarPorData(@RequestParam String data) {
        return ResponseEntity.ok(estoqueService.filtrarPorData(data));
    }

    @GetMapping("/filtro/produto")
    public ResponseEntity<List<EstoqueDTO>> filtrarPorProduto(@RequestParam Long produtoId) {
        return ResponseEntity.ok(estoqueService.listarPorProduto(produtoId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EstoqueDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody EstoqueRequest request) {
        try {
            EstoqueDTO estoque = estoqueService.atualizar(id, request);
            return ResponseEntity.ok(estoque);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        try {
            estoqueService.remover(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/resumo")
    public ResponseEntity<Map<String, Object>> resumoEstoque() {
        return ResponseEntity.ok(estoqueService.obterResumo());
    }
}
