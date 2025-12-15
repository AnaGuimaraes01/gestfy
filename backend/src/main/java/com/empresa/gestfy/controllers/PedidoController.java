package com.empresa.gestfy.controllers;

import com.empresa.gestfy.dto.pedido.PedidoRequest;
import com.empresa.gestfy.dto.pedido.PedidoItemRequest;

import com.empresa.gestfy.models.Pedido;
import com.empresa.gestfy.models.PedidoItem;
import com.empresa.gestfy.models.Produto;

import com.empresa.gestfy.repositories.PedidoRepository;
import com.empresa.gestfy.repositories.PedidoItemRepository;
import com.empresa.gestfy.repositories.ProdutoRepository;

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
            ProdutoRepository produtoRepository) {

        this.pedidoRepository = pedidoRepository;
        this.pedidoItemRepository = pedidoItemRepository;
        this.produtoRepository = produtoRepository;
    }

    // CLIENTE FINAL → CRIAR PEDIDO
    @PostMapping
    public ResponseEntity<Pedido> criarPedido(@RequestBody PedidoRequest request) {

        Pedido pedido = new Pedido();
        pedido.setData(LocalDateTime.now());

        List<PedidoItem> itens = new ArrayList<>();
        double total = 0.0;

        for (PedidoItemRequest itemReq : request.getItens()) {

            Produto produto = produtoRepository.findById(itemReq.getProdutoId())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

            PedidoItem item = new PedidoItem();
            item.setProduto(produto);
            item.setQuantidade(itemReq.getQuantidade());
            item.setPedido(pedido);

            double subtotal = produto.getPreco().doubleValue() * itemReq.getQuantidade();
            total += subtotal;

            itens.add(item);
        }

        pedido.setTotal(total);
        pedido.setItens(itens);

        pedidoRepository.save(pedido);
        pedidoItemRepository.saveAll(itens);

        return ResponseEntity.status(HttpStatus.CREATED).body(pedido);
    }
}
