package com.empresa.gestfy.controllers;

import com.empresa.gestfy.dto.pedido.PedidoItemRequest;
import com.empresa.gestfy.dto.pedido.PedidoRequest;
import com.empresa.gestfy.models.Cliente;
import com.empresa.gestfy.models.Pedido;
import com.empresa.gestfy.models.PedidoItem;
import com.empresa.gestfy.models.Produto;
import com.empresa.gestfy.repositories.ClienteRepository;
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
    private final ProdutoRepository produtoRepository;
    private final ClienteRepository clienteRepository;

    public PedidoController(
            PedidoRepository pedidoRepository,
            ProdutoRepository produtoRepository,
            ClienteRepository clienteRepository
    ) {
        this.pedidoRepository = pedidoRepository;
        this.produtoRepository = produtoRepository;
        this.clienteRepository = clienteRepository;
    }

    // =========================
    // CRIAR PEDIDO
    // =========================
    @PostMapping
    public ResponseEntity<Pedido> criarPedido(@RequestBody @Valid PedidoRequest request) {

        // 1️⃣ Busca o cliente
        Cliente cliente = clienteRepository.findById(request.clienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado: ID " + request.clienteId()));

        // 2️⃣ Cria o pedido
        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setFormaPagamento(request.formaPagamento());
        pedido.setStatus("ABERTO");
        pedido.setData(LocalDateTime.now());

        // 3️⃣ Cria os itens do pedido
        List<PedidoItem> itens = new ArrayList<>();
        for (PedidoItemRequest itemReq : request.itens()) {
            Produto produto = produtoRepository.findById(itemReq.getIdProduto())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado: ID " + itemReq.getIdProduto()));

            PedidoItem item = new PedidoItem();
            item.setPedido(pedido); // importante!
            item.setProduto(produto); // importante!
            item.setQuantidade(itemReq.getQuantidade() != null ? itemReq.getQuantidade() : 1);
            item.setPrecoUnitario(produto.getPreco() != null ? produto.getPreco() : 0.0);

            itens.add(item);
        }

        // 4️⃣ Adiciona os itens ao pedido e calcula total
        pedido.setItens(itens);
        pedido.setTotal(itens.stream().mapToDouble(i -> i.getPrecoUnitario() * i.getQuantidade()).sum());

        // 5️⃣ Salva o pedido (JPA salva os itens automaticamente)
        pedido = pedidoRepository.save(pedido);

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
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado: ID " + id));

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
