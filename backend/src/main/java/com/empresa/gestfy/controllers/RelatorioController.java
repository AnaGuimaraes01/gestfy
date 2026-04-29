package com.empresa.gestfy.controllers;

import com.empresa.gestfy.services.RelatorioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * RelatorioController
 * Responsável apenas por:
 * - Receber requisições HTTP
 * - Delegar para RelatorioService
 * - Retornar respostas HTTP
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/relatorios")
public class RelatorioController {

    private final RelatorioService relatorioService;

    public RelatorioController(RelatorioService relatorioService) {
        this.relatorioService = relatorioService;
    }

    @GetMapping("/vendas-por-dia")
    public ResponseEntity<Map<String, Object>> vendedorPorDia(@RequestParam(required = false) String data) {
        return ResponseEntity.ok(relatorioService.vendedorPorDia(data));
    }

    @GetMapping("/produtos-mais-vendidos")
    public ResponseEntity<List<Map<String, Object>>> produtosMaisVendidos(@RequestParam(defaultValue = "7") Integer periodo) {
        return ResponseEntity.ok(relatorioService.produtosMaisVendidos());
    }

    @GetMapping("/total-pedidos")
    public ResponseEntity<Map<String, Object>> totalPedidosPorPeriodo(@RequestParam(defaultValue = "7") Integer dias) {
        return ResponseEntity.ok(relatorioService.totalPedidosPorPeriodo(dias));
    }

    @GetMapping("/estoque-baixo")
    public ResponseEntity<List<Map<String, Object>>> estoqueBaixo(@RequestParam(defaultValue = "10") Integer limite) {
        return ResponseEntity.ok(relatorioService.estoqueEmAlerta(limite));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> dashboard() {
        return ResponseEntity.ok(relatorioService.dashboard());
    }
}
