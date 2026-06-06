package com.empresa.gestfy.controllers;

import com.empresa.gestfy.dto.pedido.PedidoDTO;
import com.empresa.gestfy.dto.pedido.PedidoRequest;
import com.empresa.gestfy.services.PedidoService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * PedidoController
 * Responsável apenas por:
 * - Receber requisições HTTP
 * - Validar dados de entrada
 * - Delegar para PedidoService
 * - Retornar respostas HTTP
 * 
 * Toda lógica de negócio está em PedidoService
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    /**
     * Classe interna para respostas de erro
     */
    public static class ErrorResponse {
        private int status;
        private String message;

        public ErrorResponse(int status, String message) {
            this.status = status;
            this.message = message;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    /**
     * Criar novo pedido
     * POST /api/pedidos
     */
    @PostMapping
    public ResponseEntity<?> criarPedido(@Valid @RequestBody PedidoRequest request) {
        try {
            System.out.println("\n>>> PedidoController.criarPedido() - Requisicao recebida");
            System.out.println("    Cliente: " + request.nomeCliente());
            System.out.println("    Telefone: " + request.telefoneCliente());
            System.out.println("    Itens: " + (request.itens() != null ? request.itens().size() : "NULL"));

            PedidoDTO pedido = pedidoService.criar(request);

            System.out.println(">>> PedidoController.criarPedido() - Pedido criado com sucesso");
            System.out.println("    ID: " + pedido.id());

            return ResponseEntity.status(HttpStatus.CREATED).body(pedido);

        } catch (RuntimeException e) {
            System.out.println("\n>>> PedidoController.criarPedido() - ERRO RuntimeException:");
            System.out.println("    Mensagem: " + e.getMessage());
            System.out
                    .println("    Causa: " + (e.getCause() != null ? e.getCause().getClass().getSimpleName() : "NULL"));
            if (e.getCause() != null) {
                System.out.println("    Causa Mensagem: " + e.getCause().getMessage());
            }
            e.printStackTrace();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ErrorResponse(500, "Erro ao criar pedido: " + e.getMessage()));

        } catch (Exception e) {
            System.out.println("\n>>> PedidoController.criarPedido() - ERRO Exception geral:");
            System.out.println("    Tipo: " + e.getClass().getSimpleName());
            System.out.println("    Mensagem: " + e.getMessage());
            System.out
                    .println("    Causa: " + (e.getCause() != null ? e.getCause().getClass().getSimpleName() : "NULL"));
            e.printStackTrace();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ErrorResponse(500, "Erro inesperado ao criar pedido: " + e.getClass().getSimpleName() + " - "
                            + e.getMessage()));
        }
    }

    /**
     * Listar todos os pedidos
     * GET /api/pedidos
     */
    @GetMapping
    public ResponseEntity<?> listarPedidos() {
        try {
            System.out.println("\n>>> PedidoController.listarPedidos() - Requisicao recebida");
            List<PedidoDTO> pedidos = pedidoService.listar();
            System.out.println(">>> PedidoController.listarPedidos() - " + pedidos.size() + " pedidos encontrados");
            return ResponseEntity.ok(pedidos);
        } catch (Exception e) {
            System.out.println("\n>>> PedidoController.listarPedidos() - ERRO:");
            System.out.println("    Tipo: " + e.getClass().getSimpleName());
            System.out.println("    Mensagem: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ErrorResponse(500, "Erro ao listar pedidos: " + e.getMessage()));
        }
    }

    /**
     * Buscar pedido por ID
     * GET /api/pedidos/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<PedidoDTO> buscarPorId(@PathVariable Long id) {
        return pedidoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Atualizar status do pedido
     * PUT /api/pedidos/{id}/status?status=EM_PREPARO
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<PedidoDTO> atualizarStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        try {
            PedidoDTO pedido = pedidoService.atualizarStatus(id, status);
            return ResponseEntity.ok(pedido);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Remover pedido
     * DELETE /api/pedidos/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removerPedido(@PathVariable Long id) {
        try {
            pedidoService.remover(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
