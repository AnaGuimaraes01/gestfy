package com.empresa.gestfy.controllers;

import com.empresa.gestfy.services.CaixaService;
import com.empresa.gestfy.dto.caixa.VendaRequest;
import com.empresa.gestfy.dto.caixa.VendaAgrupadaRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.Map;

/* CaixaController responsável por:
 * - Receber requisições HTTP
 * - Validar dados de entrada
 * - Delegar para CaixaService
 * - Retornar respostas HTTP
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/caixa")
public class CaixaController {

        private final CaixaService caixaService;

        public CaixaController(CaixaService caixaService) {
                this.caixaService = caixaService;
        }

        @PostMapping("/abrir")
        public ResponseEntity<Map<String, Object>> abrirCaixa() {
                Map<String, Object> resultado = caixaService.abrirCaixa();

                // Sempre retorna 200 OK com sucesso=true
                // Se caixa já estava aberto, jaAberto=true
                return ResponseEntity.ok(resultado);
        }

        @GetMapping("/buscar-produto")
        public ResponseEntity<Map<String, Object>> buscarProduto(
                        @RequestParam(required = true) String nome) {

                try {
                        if (nome == null || nome.trim().isEmpty()) {
                                Map<String, Object> erro = new java.util.HashMap<>();
                                erro.put("erro", "Por favor, informe o nome do produto para buscar");
                                return ResponseEntity.badRequest().body(erro);
                        }

                        var produtos = caixaService.buscarProdutos(nome);

                        if (produtos.isEmpty()) {
                                Map<String, Object> resultado = new java.util.HashMap<>();
                                resultado.put("erro", "Nenhum produto encontrado com o nome: " + nome);
                                resultado.put("produtos", produtos);
                                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resultado);
                        }

                        Map<String, Object> resultado = new java.util.HashMap<>();
                        resultado.put("sucesso", true);
                        resultado.put("total", produtos.size());
                        resultado.put("produtos", produtos);
                        return ResponseEntity.ok(resultado);
                } catch (Exception e) {
                        Map<String, Object> erro = new java.util.HashMap<>();
                        erro.put("erro", "Erro ao buscar produtos: " + e.getMessage());
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
                }
        }

        @PostMapping("/vender")
        public ResponseEntity<Map<String, Object>> registrarVenda(@Valid @RequestBody VendaRequest venda) {
                Map<String, Object> resultado = caixaService.registrarVenda(venda);

                if ((Boolean) resultado.get("sucesso")) {
                        return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
                } else {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultado);
                }
        }

        @PostMapping("/vender-agrupada")
        public ResponseEntity<Map<String, Object>> registrarVendaAgrupada(@Valid @RequestBody VendaAgrupadaRequest venda) {
                Map<String, Object> resultado = caixaService.registrarVendaAgrupada(venda);

                if ((Boolean) resultado.get("sucesso")) {
                        return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
                } else {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultado);
                }
        }

        @PostMapping("/fechar")
        public ResponseEntity<Map<String, Object>> fecharCaixa() {
                Map<String, Object> resultado = caixaService.fecharCaixa();

                if ((Boolean) resultado.get("sucesso")) {
                        return ResponseEntity.ok(resultado);
                } else {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resultado);
                }
        }

        @GetMapping("/vendas-do-dia")
        public ResponseEntity<Map<String, Object>> listarVendasDoDia() {
                return ResponseEntity.ok(caixaService.listarVendasDoDia());
        }

        @GetMapping("/status")
        public ResponseEntity<Map<String, Object>> obterStatus() {
                return ResponseEntity.ok(caixaService.obterStatus());
        }
}
