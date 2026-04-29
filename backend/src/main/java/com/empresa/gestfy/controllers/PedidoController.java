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
     * Criar novo pedido
     * POST /api/pedidos
     */
    @PostMapping
    public ResponseEntity<PedidoDTO> criarPedido(@Valid @RequestBody PedidoRequest request) {
        PedidoDTO pedido = pedidoService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(pedido);
    }

    /**
     * Listar todos os pedidos
     * GET /api/pedidos
     */
    @GetMapping
    public ResponseEntity<List<PedidoDTO>> listarPedidos() {
        return ResponseEntity.ok(pedidoService.listar());
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
