package com.empresa.gestfy.controllers;

import com.empresa.gestfy.dto.estoque.EstoqueDTO;
import com.empresa.gestfy.dto.estoque.EstoqueRequest;
import com.empresa.gestfy.models.Estoque;
import com.empresa.gestfy.repositories.EstoqueRepository;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/estoque")
public class EstoqueController {

    private final EstoqueRepository estoqueRepository;

    public EstoqueController(EstoqueRepository estoqueRepository) {
        this.estoqueRepository = estoqueRepository;
    }

    // ========================= CREATE =========================
    @PostMapping
    public ResponseEntity<EstoqueDTO> criar(@Valid @RequestBody EstoqueRequest request) {
        Estoque estoque = new Estoque();
        estoque.setProdutoId(request.produtoId());
        estoque.setTipoMovimento(request.tipoMovimento()); // ENTRADA ou SAIDA
        estoque.setQuantidade(request.quantidade());
        estoque.setDataMovimento(LocalDateTime.now());

        Estoque salvo = estoqueRepository.save(estoque);
        return ResponseEntity.status(201).body(toDTO(salvo));
    }

    // ========================= LIST ALL =========================
    @GetMapping
    public ResponseEntity<List<EstoqueDTO>> listar() {
        List<EstoqueDTO> lista = estoqueRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
        return ResponseEntity.ok(lista);
    }

    // ========================= GET BY ID =========================
    @GetMapping("/{id}")
    public ResponseEntity<EstoqueDTO> buscarPorId(@PathVariable Long id) {
        Estoque estoque = estoqueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movimentação não encontrada"));
        return ResponseEntity.ok(toDTO(estoque));
    }

    // ========================= FILTER BY TIPO =========================
    @GetMapping("/filtro/tipo")
    public ResponseEntity<List<EstoqueDTO>> filtrarPorTipo(@RequestParam String tipo) {
        List<EstoqueDTO> lista = estoqueRepository.findAll()
                .stream()
                .filter(e -> e.getTipoMovimento().equalsIgnoreCase(tipo))
                .map(this::toDTO)
                .toList();
        return ResponseEntity.ok(lista);
    }

    // ========================= FILTER BY DATA =========================
    @GetMapping("/filtro/data")
    public ResponseEntity<List<EstoqueDTO>> filtrarPorData(@RequestParam String data) {
        LocalDate dataFiltro = LocalDate.parse(data);
        List<EstoqueDTO> lista = estoqueRepository.findAll()
                .stream()
                .filter(e -> e.getDataMovimento().toLocalDate().equals(dataFiltro))
                .map(this::toDTO)
                .toList();
        return ResponseEntity.ok(lista);
    }

    // ========================= FILTER BY PRODUTO =========================
    @GetMapping("/filtro/produto")
    public ResponseEntity<List<EstoqueDTO>> filtrarPorProduto(@RequestParam Long produtoId) {
        List<EstoqueDTO> lista = estoqueRepository.findAll()
                .stream()
                .filter(e -> e.getProdutoId().equals(produtoId))
                .map(this::toDTO)
                .toList();
        return ResponseEntity.ok(lista);
    }

    // ========================= UPDATE =========================
    @PutMapping("/{id}")
    public ResponseEntity<EstoqueDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody EstoqueRequest request) {

        Estoque estoque = estoqueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movimentação não encontrada"));

        estoque.setTipoMovimento(request.tipoMovimento());
        estoque.setQuantidade(request.quantidade());

        Estoque atualizado = estoqueRepository.save(estoque);
        return ResponseEntity.ok(toDTO(atualizado));
    }

    // ========================= DELETE =========================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (!estoqueRepository.existsById(id)) {
            throw new RuntimeException("Movimentação não encontrada");
        }
        estoqueRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ========================= RESUMO ESTOQUE =========================
    @GetMapping("/resumo")
    public ResponseEntity<Map<String, Object>> resumoEstoque() {
        List<Estoque> movimentos = estoqueRepository.findAll();

        Integer totalEntradas = movimentos.stream()
                .filter(e -> "ENTRADA".equals(e.getTipoMovimento()))
                .mapToInt(Estoque::getQuantidade)
                .sum();

        Integer totalSaidas = movimentos.stream()
                .filter(e -> "SAIDA".equals(e.getTipoMovimento()))
                .mapToInt(Estoque::getQuantidade)
                .sum();

        Integer saldoTotal = totalEntradas - totalSaidas;

        Map<String, Object> response = new HashMap<>();
        response.put("totalEntradas", totalEntradas);
        response.put("totalSaidas", totalSaidas);
        response.put("saldoTotal", saldoTotal);
        response.put("totalMovimentacoes", movimentos.size());

        return ResponseEntity.ok(response);
    }

    // ========================= CONVERTER =========================
    private EstoqueDTO toDTO(Estoque estoque) {
        return new EstoqueDTO(
                estoque.getId(),
                estoque.getProdutoId(),
                estoque.getTipoMovimento(),
                estoque.getDataMovimento(),
                estoque.getQuantidade()
        );
    }
}
