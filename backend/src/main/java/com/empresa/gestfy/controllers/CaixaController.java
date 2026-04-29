package com.empresa.gestfy.controllers;

import com.empresa.gestfy.services.CaixaService;
import com.empresa.gestfy.dto.caixa.VendaRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.Map;

/**
 * CaixaController
 * Responsável apenas por:
 * - Receber requisições HTTP
 * - Validar dados de entrada
 * - Delegar para CaixaService
 * - Retornar respostas HTTP
 * 
 * Toda lógica de negócio está em CaixaService
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/caixa")
public class CaixaController {

        private final CaixaService caixaService;

        public CaixaController(CaixaService caixaService) {
                this.caixaService = caixaService;
        }

        // ========================================
        // 1. ABERTURA DO CAIXA
        // ========================================
        /**
         * Abre o caixa do dia.
         * POST /api/caixa/abrir
         */
        @PostMapping("/abrir")
        public ResponseEntity<Map<String, Object>> abrirCaixa() {
                Map<String, Object> resultado = caixaService.abrirCaixa();

                if ((Boolean) resultado.get("sucesso")) {
                        return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
                } else {
                        return ResponseEntity.status(HttpStatus.CONFLICT).body(resultado);
                }
        }

        // ========================================
        // 2. BUSCAR PRODUTOS POR NOME (PARCIAL)
        // ========================================
        /**
         * Busca produtos pelo nome (busca parcial, case-insensitive).
         * GET /api/caixa/buscar-produto?nome=sorvete
         */
        @GetMapping("/buscar-produto")
        public ResponseEntity<Map<String, Object>> buscarProduto(
                        @RequestParam(required = true) String nome) {

                // Valida se o nome foi informado
                if (nome == null || nome.trim().isEmpty()) {
                        return ResponseEntity.badRequest()
                                        .body(Map.of(
                                                        "erro", "Por favor, informe o nome do produto para buscar"));
                }

                // Busca produtos via service
                var produtos = caixaService.buscarProdutos(nome);

                // Se não encontrou nenhum produto
                if (produtos.isEmpty()) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                        .body(Map.of(
                                                        "erro", "Nenhum produto encontrado com o nome: " + nome,
                                                        "produtos", produtos));
                }

                return ResponseEntity.ok(Map.of(
                                "sucesso", true,
                                "total", produtos.size(),
                                "produtos", produtos));
        }

        // ========================================
        // 3. REGISTRAR VENDA
        // ========================================
        /**
         * Registra uma venda no caixa.
         * POST /api/caixa/vender
         * Body: { produtoId, quantidade, valorRecebido }
         */
        @PostMapping("/vender")
        public ResponseEntity<Map<String, Object>> registrarVenda(@Valid @RequestBody VendaRequest venda) {
                Map<String, Object> resultado = caixaService.registrarVenda(venda);

                if ((Boolean) resultado.get("sucesso")) {
                        return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
                } else {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultado);
                }
        }

        // ========================================
        // 4. FECHAR CAIXA DO DIA
        // ========================================
        /**
         * Fecha o caixa do dia.
         * POST /api/caixa/fechar
         */
        @PostMapping("/fechar")
        public ResponseEntity<Map<String, Object>> fecharCaixa() {
                Map<String, Object> resultado = caixaService.fecharCaixa();

                if ((Boolean) resultado.get("sucesso")) {
                        return ResponseEntity.ok(resultado);
                } else {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resultado);
                }
        }

        // ========================================
        // 5. LISTAR VENDAS DO DIA
        // ========================================
        /**
         * Lista todas as vendas registradas no dia.
         * GET /api/caixa/vendas-do-dia
         */
        @GetMapping("/vendas-do-dia")
        public ResponseEntity<Map<String, Object>> listarVendasDoDia() {
                return ResponseEntity.ok(caixaService.listarVendasDoDia());
        }

        // ========================================
        // 6. OBTER STATUS DO CAIXA
        // ========================================
        /**
         * Verifica o status atual do caixa.
         * GET /api/caixa/status
         */
        @GetMapping("/status")
        public ResponseEntity<Map<String, Object>> obterStatus() {
                return ResponseEntity.ok(caixaService.obterStatus());
        }
}
