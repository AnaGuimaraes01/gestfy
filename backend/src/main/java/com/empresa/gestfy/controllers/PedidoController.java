package com.empresa.gestfy.controllers;

import com.empresa.gestfy.dto.pedido.PedidoDTO;
import com.empresa.gestfy.dto.pedido.PedidoItemDTO;
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
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;
    private final ClienteRepository clienteRepository;

    public PedidoController(
            PedidoRepository pedidoRepository,
            ProdutoRepository produtoRepository,
            ClienteRepository clienteRepository) {
        this.pedidoRepository = pedidoRepository;
        this.produtoRepository = produtoRepository;
        this.clienteRepository = clienteRepository;
    }

    // =========================
    // CRIAR PEDIDO
    // =========================
    @PostMapping
    public ResponseEntity<PedidoDTO> criarPedido(@RequestBody @Valid PedidoRequest request) {

        // 1️⃣ Busca o cliente
        Cliente cliente = clienteRepository.findById(request.clienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado: ID " + request.clienteId()));

        // 2️⃣ Cria o pedido
        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setFormaPagamento(request.formaPagamento());
        pedido.setFormaRecebimento(request.formaRecebimento());
        pedido.setStatus("RECEBIDO");
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

        // 4️⃣ Adiciona os itens ao pedido
        pedido.setItens(itens);

        // 5️⃣ Calcula e define o total
        Double totalCalculado = itens.stream()
                .mapToDouble(i -> i.getPrecoUnitario() * i.getQuantidade())
                .sum();
        pedido.setTotal(totalCalculado > 0 ? totalCalculado : 0.0);

        // 6️⃣ Salva o pedido (JPA salva os itens automaticamente)
        pedido = pedidoRepository.save(pedido);

        return ResponseEntity.status(HttpStatus.CREATED).body(mapToDTO(pedido));
    }

    // =========================
    // LISTAR TODOS
    // =========================
    @GetMapping
    public ResponseEntity<List<PedidoDTO>> listarPedidos() {
        List<PedidoDTO> pedidos = pedidoRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(pedidos);
    }

    // =========================
    // BUSCAR POR ID
    // =========================
    @GetMapping("/{id}")
    public ResponseEntity<PedidoDTO> buscarPorId(@PathVariable Long id) {
        return pedidoRepository.findById(id)
                .map(p -> ResponseEntity.ok(mapToDTO(p)))
                .orElse(ResponseEntity.notFound().build());
    }

    // =========================
    // ATUALIZAR STATUS
    // =========================
    @PutMapping("/{id}/status")
    public ResponseEntity<PedidoDTO> atualizarStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado: ID " + id));

        pedido.setStatus(status);
        pedidoRepository.save(pedido);

        return ResponseEntity.ok(mapToDTO(pedido));
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

    // =========================
    // HELPER: MAPEAR PARA DTO
    // =========================
    private PedidoDTO mapToDTO(Pedido pedido) {
        List<PedidoItemDTO> itensDTO = pedido.getItens().stream()
                .map(item -> new PedidoItemDTO(
                        item.getId(),
                        item.getProduto().getId(),
                        item.getProduto().getNome(),
                        item.getPrecoUnitario() != null ? item.getPrecoUnitario() : 0.0,
                        item.getQuantidade() != null ? item.getQuantidade() : 0,
                        (item.getPrecoUnitario() != null ? item.getPrecoUnitario() : 0.0) *
                                (item.getQuantidade() != null ? item.getQuantidade() : 0)))
                .collect(Collectors.toList());

        // Garante que total nunca seja null
        Double totalFinal = pedido.getTotal();
        if (totalFinal == null) {
            totalFinal = itensDTO.stream()
                    .mapToDouble(PedidoItemDTO::getSubtotal)
                    .sum();
        }

        return new PedidoDTO(
                pedido.getId(),
                pedido.getCliente().getNome(),
                pedido.getCliente().getTelefone(),
                pedido.getFormaPagamento(),
                pedido.getFormaRecebimento(),
                pedido.getStatus(),
                totalFinal > 0 ? totalFinal : 0.0,
                pedido.getData(),
                itensDTO);
    }
}
