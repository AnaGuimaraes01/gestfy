package com.empresa.gestfy.services;

import com.empresa.gestfy.models.Pedido;
import com.empresa.gestfy.models.Produto;
import com.empresa.gestfy.repositories.CaixaRepository;
import com.empresa.gestfy.repositories.PedidoRepository;
import com.empresa.gestfy.repositories.ProdutoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * RelatorioService
 * Responsável por gerar relatórios e análises do sistema.
 */
@Service
public class RelatorioService {

    private final CaixaRepository caixaRepository;
    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;

    public RelatorioService(
            CaixaRepository caixaRepository,
            PedidoRepository pedidoRepository,
            ProdutoRepository produtoRepository) {
        this.caixaRepository = caixaRepository;
        this.pedidoRepository = pedidoRepository;
        this.produtoRepository = produtoRepository;
    }

    /**
     * Gerar relatório de vendas por período
     */
    public Map<String, Object> relatorioVendasPorPeriodo(String dataInicio, String dataFim) {
        LocalDate inicio = LocalDate.parse(dataInicio);
        LocalDate fim = LocalDate.parse(dataFim);

        List<Pedido> pedidos = pedidoRepository.findAll().stream()
                .filter(p -> {
                    LocalDate dataPedido = p.getData().toLocalDate();
                    return !dataPedido.isBefore(inicio) && !dataPedido.isAfter(fim);
                })
                .collect(Collectors.toList());

        Double totalVendas = pedidos.stream()
                .mapToDouble(Pedido::getTotal)
                .sum();

        Long totalPedidos = (long) pedidos.size();

        Map<String, Object> relatorio = new HashMap<>();
        relatorio.put("dataInicio", inicio);
        relatorio.put("dataFim", fim);
        relatorio.put("totalPedidos", totalPedidos);
        relatorio.put("totalVendas", totalVendas);
        relatorio.put("ticketMedio", totalPedidos > 0 ? totalVendas / totalPedidos : 0.0);

        return relatorio;
    }

    /**
     * Produtos mais vendidos
     */
    public List<Map<String, Object>> produtosMaisVendidos() {
        List<Pedido> todosOsPedidos = pedidoRepository.findAll();

        return todosOsPedidos.stream()
                .flatMap(p -> p.getItens().stream())
                .collect(Collectors.groupingBy(
                        item -> item.getProduto().getNome(),
                        Collectors.summingInt(item -> item.getQuantidade())))
                .entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(10)
                .map(entry -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("produto", entry.getKey());
                    item.put("quantidadeVendida", entry.getValue());
                    return item;
                })
                .collect(Collectors.toList());
    }

    /**
     * Estoque em alerta (baixo)
     */
    public List<Map<String, Object>> estoqueEmAlerta(Integer limite) {
        return produtoRepository.findAll().stream()
                .filter(p -> p.getQuantidade() != null && p.getQuantidade() <= limite)
                .map(p -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", p.getId());
                    item.put("nome", p.getNome());
                    item.put("quantidade", p.getQuantidade());
                    item.put("preco", p.getPreco());
                    return item;
                })
                .collect(Collectors.toList());
    }

    /**
     * Dashboard com indicadores gerenciais
     */
    public Map<String, Object> dashboard() {
        List<Pedido> todosOsPedidos = pedidoRepository.findAll();
        List<Produto> todosOsProdutos = produtoRepository.findAll();

        Double totalVendasGeral = todosOsPedidos.stream()
                .mapToDouble(Pedido::getTotal)
                .sum();

        Integer totalProdutosCadastrados = (int) todosOsProdutos.size();
        Integer totalPedidosGeral = todosOsPedidos.size();

        Long produtosBaixoEstoque = todosOsProdutos.stream()
                .filter(p -> p.getQuantidade() != null && p.getQuantidade() <= 5)
                .count();

        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("totalVendas", totalVendasGeral);
        dashboard.put("totalPedidos", totalPedidosGeral);
        dashboard.put("totalProdutos", totalProdutosCadastrados);
        dashboard.put("produtosBaixoEstoque", produtosBaixoEstoque);
        dashboard.put("ticketMedio", totalPedidosGeral > 0 ? totalVendasGeral / totalPedidosGeral : 0.0);

        return dashboard;
    }

    /**
     * Vendas por dia
     */
    public Map<String, Object> vendedorPorDia(String data) {
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

        return response;
    }

    /**
     * Total de pedidos por período
     */
    public Map<String, Object> totalPedidosPorPeriodo(Integer dias) {
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

        return response;
    }
}
