package com.empresa.gestfy.controllers;

import com.empresa.gestfy.models.Estoque;
import com.empresa.gestfy.models.Pedido;
import com.empresa.gestfy.models.PedidoItem;
import com.empresa.gestfy.repositories.EstoqueRepository;
import com.empresa.gestfy.repositories.PedidoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/relatorios")
public class RelatorioController {

    private final PedidoRepository pedidoRepository;
    private final EstoqueRepository estoqueRepository;

    public RelatorioController(PedidoRepository pedidoRepository, EstoqueRepository estoqueRepository) {
        this.pedidoRepository = pedidoRepository;
        this.estoqueRepository = estoqueRepository;
    }

    // =========================
    // VENDAS POR DIA
    // =========================
    @GetMapping("/vendas-por-dia")
    public ResponseEntity<Map<String, Object>> vendedorPorDia(@RequestParam(required = false) String data) {
        LocalDate dataFiltro = (data != null && !data.isEmpty()) ? LocalDate.parse(data) : LocalDate.now();

        List<Pedido> pedidosDodia = pedidoRepository.findAll()
                .stream()
                .filter(p -> p.getData().toLocalDate().equals(dataFiltro) && "FINALIZADO".equals(p.getStatus()))
                .collect(Collectors.toList());

        Double totalVendas = pedidosDodia.stream()
                .mapToDouble(Pedido::getTotal)
                .sum();

        Map<String, Object> response = new HashMap<>();
        response.put("data", dataFiltro);
        response.put("totalVendas", totalVendas);
        response.put("quantidadePedidos", pedidosDodia.size());
        response.put("pedidos", pedidosDodia.stream()
                .map(p -> new HashMap<String, Object>() {{
                    put("id", p.getId());
                    put("cliente", p.getCliente().getNome());
                    put("total", p.getTotal());
                }})
                .collect(Collectors.toList()));

        return ResponseEntity.ok(response);
    }

    // =========================
    // PRODUTOS MAIS VENDIDOS
    // =========================
    @GetMapping("/produtos-mais-vendidos")
    public ResponseEntity<List<Map<String, Object>>> produtosMaisVendidos(@RequestParam(defaultValue = "7") Integer periodo) {
        LocalDateTime dataLimite = LocalDateTime.now().minusDays(periodo);

        Map<Long, Integer> produtosVendidos = new HashMap<>();
        Map<Long, String> nomesProdutos = new HashMap<>();
        Map<Long, Double> precos = new HashMap<>();

        pedidoRepository.findAll()
                .stream()
                .filter(p -> p.getData().isAfter(dataLimite) && "FINALIZADO".equals(p.getStatus()))
                .forEach(pedido -> {
                    for (PedidoItem item : pedido.getItens()) {
                        Long produtoId = item.getProduto().getId();
                        produtosVendidos.put(produtoId, produtosVendidos.getOrDefault(produtoId, 0) + item.getQuantidade());
                        nomesProdutos.put(produtoId, item.getProduto().getNome());
                        precos.put(produtoId, item.getPrecoUnitario());
                    }
                });

        List<Map<String, Object>> resultado = produtosVendidos.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .map(entry -> new HashMap<String, Object>() {{
                    put("produtoId", entry.getKey());
                    put("nome", nomesProdutos.get(entry.getKey()));
                    put("quantidade", entry.getValue());
                    put("preco", precos.get(entry.getKey()));
                }})
                .collect(Collectors.toList());

        return ResponseEntity.ok(resultado);
    }

    // =========================
    // TOTAL DE PEDIDOS POR PERÍODO
    // =========================
    @GetMapping("/total-pedidos")
    public ResponseEntity<Map<String, Object>> totalPedidosPorPeriodo(@RequestParam(defaultValue = "7") Integer dias) {
        LocalDateTime dataLimite = LocalDateTime.now().minusDays(dias);

        List<Pedido> pedidos = pedidoRepository.findAll()
                .stream()
                .filter(p -> p.getData().isAfter(dataLimite))
                .collect(Collectors.toList());

        Long totalFinalizados = pedidos.stream()
                .filter(p -> "FINALIZADO".equals(p.getStatus()))
                .count();

        Long totalPendentes = pedidos.stream()
                .filter(p -> !p.getStatus().equals("FINALIZADO"))
                .count();

        Double receitaTotal = pedidos.stream()
                .filter(p -> "FINALIZADO".equals(p.getStatus()))
                .mapToDouble(Pedido::getTotal)
                .sum();

        Map<String, Object> response = new HashMap<>();
        response.put("periodo", dias + " dias");
        response.put("totalPedidos", pedidos.size());
        response.put("pedidosFinalizados", totalFinalizados);
        response.put("pedidosPendentes", totalPendentes);
        response.put("receitaTotal", receitaTotal);

        return ResponseEntity.ok(response);
    }

    // =========================
    // ESTOQUE BAIXO
    // =========================
    @GetMapping("/estoque-baixo")
    public ResponseEntity<List<Map<String, Object>>> estoqueBaixo(@RequestParam(defaultValue = "10") Integer limite) {
        List<Estoque> movimentos = estoqueRepository.findAll();

        // Agrupa movimentos por produto para calcular estoque
        Map<Long, Integer> estoquePorProduto = new HashMap<>();
        movimentos.forEach(mov -> {
            Long produtoId = mov.getProdutoId();
            Integer quantidade = estoquePorProduto.getOrDefault(produtoId, 0);

            if ("ENTRADA".equals(mov.getTipoMovimento())) {
                quantidade += mov.getQuantidade();
            } else if ("SAIDA".equals(mov.getTipoMovimento())) {
                quantidade -= mov.getQuantidade();
            }

            estoquePorProduto.put(produtoId, quantidade);
        });

        List<Map<String, Object>> resultado = estoquePorProduto.entrySet().stream()
                .filter(entry -> entry.getValue() <= limite)
                .map(entry -> new HashMap<String, Object>() {{
                    put("produtoId", entry.getKey());
                    put("estoque", entry.getValue());
                }})
                .collect(Collectors.toList());

        return ResponseEntity.ok(resultado);
    }
}
