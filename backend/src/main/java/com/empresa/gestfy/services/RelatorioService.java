package com.empresa.gestfy.services;

import com.empresa.gestfy.config.DataHoraBrasil;
import com.empresa.gestfy.models.Pedido;
import com.empresa.gestfy.models.PedidoItem;
import com.empresa.gestfy.models.Produto;
import com.empresa.gestfy.repositories.CaixaRepository;
import com.empresa.gestfy.repositories.PedidoRepository;
import com.empresa.gestfy.repositories.ProdutoRepository;
import com.empresa.gestfy.dto.relatorio.ProdutoMaisVendidoDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
         * Produtos mais vendidos (top 5 com valor total)
         */
        @Transactional(readOnly = true)
        public List<ProdutoMaisVendidoDTO> produtosMaisVendidos() {
                List<Pedido> todosOsPedidos = pedidoRepository.findAllWithRelationships();

                return todosOsPedidos.stream()
                                .flatMap(p -> p.getItens().stream())
                                .filter(item -> item.getProduto() != null)
                                .collect(Collectors.groupingBy(
                                                PedidoItem::getProduto,
                                                Collectors.collectingAndThen(
                                                                Collectors.toList(),
                                                                items -> {
                                                                        Integer qtd = items.stream()
                                                                                        .mapToInt(PedidoItem::getQuantidade)
                                                                                        .sum();
                                                                        Double valor = items.stream()
                                                                                        .mapToDouble(item -> item
                                                                                                        .getQuantidade()
                                                                                                        * (item.getPrecoUnitario() != null
                                                                                                                        ? item.getPrecoUnitario()
                                                                                                                        : 0.0))
                                                                                        .sum();
                                                                        return new Object[] { qtd, valor };
                                                                })))
                                .entrySet().stream()
                                .sorted((a, b) -> {
                                        Integer qtdA = (Integer) ((Object[]) a.getValue())[0];
                                        Integer qtdB = (Integer) ((Object[]) b.getValue())[0];
                                        return qtdB.compareTo(qtdA);
                                })
                                .limit(5)
                                .map(entry -> {
                                        Object[] agg = (Object[]) entry.getValue();
                                        Integer qtd = (Integer) agg[0];
                                        Double valor = (Double) agg[1];
                                        return new ProdutoMaisVendidoDTO(entry.getKey().getNome(), qtd, valor);
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
                                .filter(p -> p.getData().toLocalDate().equals(dataFiltro)
                                                && "FINALIZADO".equals(p.getStatus()))
                                .collect(Collectors.toList());

                Double totalVendas = pedidosDodia.stream()
                                .mapToDouble(Pedido::getTotal)
                                .sum();

                Map<String, Object> response = new HashMap<>();
                response.put("data", dataFiltro);
                response.put("totalVendas", totalVendas);
                response.put("quantidadePedidos", pedidosDodia.size());
                response.put("pedidos", pedidosDodia.stream()
                                .map(p -> new HashMap<String, Object>() {
                                        {
                                                put("id", p.getId());
                                                put("cliente", p.getCliente().getNome());
                                                put("total", p.getTotal());
                                        }
                                })
                                .collect(Collectors.toList()));

                return response;
        }

        /**
         * Vendas por período (múltiplos dias)
         */
        public Map<String, Object> vendedorPorPeriodo(String dataInicio, String dataFim) {
                LocalDate inicio = LocalDate.parse(dataInicio);
                LocalDate fim = LocalDate.parse(dataFim);

                List<Pedido> pedidosPeriodo = pedidoRepository.findAll()
                                .stream()
                                .filter(p -> {
                                        LocalDate dataPedido = p.getData().toLocalDate();
                                        return !dataPedido.isBefore(inicio) && !dataPedido.isAfter(fim)
                                                        && "FINALIZADO".equals(p.getStatus());
                                })
                                .collect(Collectors.toList());

                Double totalVendas = pedidosPeriodo.stream()
                                .mapToDouble(Pedido::getTotal)
                                .sum();

                List<Map<String, Object>> vendedorPorDia = pedidosPeriodo.stream()
                                .collect(Collectors.groupingBy(p -> p.getData().toLocalDate()))
                                .entrySet().stream()
                                .sorted(Map.Entry.comparingByKey())
                                .map(entry -> {
                                        LocalDate data = entry.getKey();
                                        List<Pedido> pedidosDia = entry.getValue();
                                        Double totalDia = pedidosDia.stream().mapToDouble(Pedido::getTotal).sum();
                                        Integer qtdDia = pedidosDia.size();

                                        Map<String, Object> dia = new HashMap<>();
                                        dia.put("data", data);
                                        dia.put("quantidadePedidos", qtdDia);
                                        dia.put("totalVendas", totalDia);
                                        dia.put("ticketMedio", qtdDia > 0 ? totalDia / qtdDia : 0.0);
                                        return dia;
                                })
                                .collect(Collectors.toList());

                Map<String, Object> response = new HashMap<>();
                response.put("dataInicio", inicio);
                response.put("dataFim", fim);
                response.put("totalVendas", totalVendas);
                response.put("quantidadePedidos", pedidosPeriodo.size());
                response.put("ticketMedio", pedidosPeriodo.size() > 0 ? totalVendas / pedidosPeriodo.size() : 0.0);
                response.put("vendedorPorDia", vendedorPorDia);

                return response;
        }

        /**
         * Total de pedidos por período
         */
        public Map<String, Object> totalPedidosPorPeriodo(Integer dias) {
                LocalDateTime dataLimite = DataHoraBrasil.agora().minusDays(dias);

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

        /**
         * Status de produtos com informações de estoque
         */
        public List<Map<String, Object>> statusProdutos() {
                return produtoRepository.findAll().stream()
                                .map(p -> {
                                        Map<String, Object> item = new HashMap<>();
                                        item.put("id", p.getId());
                                        item.put("nome", p.getNome());
                                        item.put("quantidade", p.getQuantidade() != null ? p.getQuantidade() : 0);
                                        item.put("preco", p.getPreco());

                                        Integer qtd = p.getQuantidade() != null ? p.getQuantidade() : 0;
                                        String status = qtd <= 0 ? "Em Falta" : qtd <= 5 ? "Baixo" : "Disponível";
                                        item.put("status", status);

                                        return item;
                                })
                                .collect(Collectors.toList());
        }
}
