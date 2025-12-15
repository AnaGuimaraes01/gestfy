package com.empresa.gestfy.controllers;

import com.empresa.gestfy.dto.pedido.PedidoItemRequest;
import com.empresa.gestfy.dto.pedido.PedidoRequest;
import com.empresa.gestfy.models.Pedido;
import com.empresa.gestfy.models.PedidoItem;
import com.empresa.gestfy.models.Produto;
import com.empresa.gestfy.repositories.PedidoItemRepository;
import com.empresa.gestfy.repositories.PedidoRepository;
import com.empresa.gestfy.repositories.ProdutoRepository;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoRepository pedidoRepository;
    private final PedidoItemRepository pedidoItemRepository;
    private final ProdutoRepository produtoRepository;

    public PedidoController(
            PedidoRepository pedidoRepository,
            PedidoItemRepository pedidoItemRepository,
            ProdutoRepository produtoRepository
    ) {
        this.pedidoRepository = pedidoRepository;
        this.pedidoItemRepository = pedidoItemRepository;
        this.produtoRepository = produtoRepository;
    }

    // =========================
    // CRIAR PEDIDO
    // =========================
    @PostMapping
    public ResponseEntity<Pedido> criarPedido(
            @RequestBody @Valid PedidoRequest request
    ) {

        // 1️⃣ Cria o pedido
        Pedido pedido = new Pedido();
        pedido.setNomeCliente(request.nomeCliente());
        pedido.setTelefone(request.telefone());
        pedido.setFormaPagamento(request.formaPagamento());

        pedido.setStatus("ABERTO"); // backend controla
        pedido.setData(LocalDateTime.now());

        pedido = pedidoRepository.save(pedido);

        double total = 0.0;
        List<PedidoItem> itens = new ArrayList<>();

        // 2️⃣ Cria os itens
        for (PedidoItemRequest itemReq : request.itens()) {

            Produto produto = produtoRepository.findById(itemReq.getProdutoId())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

            PedidoItem item = new PedidoItem();
            item.setPedido(pedido);
            item.setProduto(produto);
            item.setQuantidade(itemReq.getQuantidade());
            item.setPrecoUnitario(produto.getPreco());

            double subtotal = produto.getPreco() * itemReq.getQuantidade();
            total += subtotal;

            itens.add(item);
        }

        // 3️⃣ Salva itens e atualiza pedido
        pedidoItemRepository.saveAll(itens);

        pedido.setTotal(total);
        pedido.setItens(itens);

        pedidoRepository.save(pedido);

        return ResponseEntity.status(HttpStatus.CREATED).body(pedido);
    }

    // =========================
    // LISTAR TODOS
    // =========================
    @GetMapping
    public ResponseEntity<List<Pedido>> listarPedidos() {
        return ResponseEntity.ok(pedidoRepository.findAll());
    }

    // =========================
    // BUSCAR POR ID
    // =========================
    @GetMapping("/{id}")
    public ResponseEntity<Pedido> buscarPorId(@PathVariable Long id) {
        return pedidoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // =========================
    // ATUALIZAR STATUS
    // =========================
    @PutMapping("/{id}/status")
    public ResponseEntity<Pedido> atualizarStatus(
            @PathVariable Long id,
            @RequestParam String status
    ) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        pedido.setStatus(status);
        pedidoRepository.save(pedido);

        return ResponseEntity.ok(pedido);
    }

    // =========================
    // REMOVER PEDIDO
    // =========================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removerPedido(@PathVariable Long id) {

        if (!pedidoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        pedidoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
