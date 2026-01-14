package com.empresa.gestfy.controllers;

import com.empresa.gestfy.models.Pedido;
import com.empresa.gestfy.repositories.PedidoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/caixa")
public class CaixaController {

    private final PedidoRepository pedidoRepository;

    public CaixaController(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    // =========================
    // BUSCAR VENDAS DO DIA
    // =========================
    @GetMapping("/dia")
    public ResponseEntity<Map<String, Object>> obterCaixaDoDia(@RequestParam(required = false) String data) {
        LocalDate dataFiltro = (data != null && !data.isEmpty()) ? LocalDate.parse(data) : LocalDate.now();

        // Buscar todos os pedidos do dia (completos ou parciais)
        LocalDateTime inicioDodia = dataFiltro.atStartOfDay();
        LocalDateTime fimDodia = dataFiltro.atTime(LocalTime.MAX);

        List<Pedido> pedidosDoDia = pedidoRepository.findAll()
                .stream()
                .filter(p -> p.getData() != null && 
                        !p.getData().isBefore(inicioDodia) && 
                        !p.getData().isAfter(fimDodia) &&
                        "FINALIZADO".equals(p.getStatus()))
                .collect(Collectors.toList());

        // Calcular totais
        Double totalDia = pedidosDoDia.stream()
                .mapToDouble(p -> p.getTotal() != null ? p.getTotal() : 0.0)
                .sum();

        // Criar lista de registros para tabela
        List<Map<String, Object>> registros = pedidosDoDia.stream()
                .map(p -> {
                    Map<String, Object> reg = new HashMap<>();
                    reg.put("id", p.getId());
                    reg.put("descricao", "Pedido #" + p.getId() + " - " + (p.getCliente() != null ? p.getCliente().getNome() : "Cliente"));
                    reg.put("saldo", p.getTotal() != null ? p.getTotal() : 0.0);
                    reg.put("data", p.getData());
                    return reg;
                })
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("data", dataFiltro);
        response.put("totalDia", totalDia);
        response.put("quantidadeRegistros", pedidosDoDia.size());
        response.put("registros", registros);

        return ResponseEntity.ok(response);
    }

    // =========================
    // RELATÓRIO DE FECHAMENTO DO DIA
    // =========================
    @GetMapping("/relatorio/fechamento")
    public ResponseEntity<Map<String, Object>> fechamentoDia(@RequestParam(required = false) String data) {
        LocalDate dataFiltro = (data != null && !data.isEmpty()) ? LocalDate.parse(data) : LocalDate.now();

        LocalDateTime inicioDodia = dataFiltro.atStartOfDay();
        LocalDateTime fimDodia = dataFiltro.atTime(LocalTime.MAX);

        List<Pedido> pedidosDoDia = pedidoRepository.findAll()
                .stream()
                .filter(p -> p.getData() != null && 
                        !p.getData().isBefore(inicioDodia) && 
                        !p.getData().isAfter(fimDodia) &&
                        "FINALIZADO".equals(p.getStatus()))
                .collect(Collectors.toList());

        Double totalVendas = pedidosDoDia.stream()
                .mapToDouble(p -> p.getTotal() != null ? p.getTotal() : 0.0)
                .sum();

        // Totalizadores por forma de pagamento
        Map<String, Double> vendaseporFormaPagamento = new HashMap<>();
        pedidosDoDia.forEach(p -> {
            String forma = p.getFormaPagamento() != null ? p.getFormaPagamento() : "Indefinido";
            vendaseporFormaPagamento.put(forma, 
                    vendaseporFormaPagamento.getOrDefault(forma, 0.0) + (p.getTotal() != null ? p.getTotal() : 0.0));
        });

        List<Map<String, Object>> detalhes = pedidosDoDia.stream()
                .map(p -> {
                    Map<String, Object> det = new HashMap<>();
                    det.put("id", p.getId());
                    det.put("descricao", "Pedido #" + p.getId());
                    det.put("valor", p.getTotal());
                    det.put("forma", p.getFormaPagamento());
                    det.put("data", p.getData());
                    return det;
                })
                .collect(Collectors.toList());

        Map<String, Object> fechamento = new HashMap<>();
        fechamento.put("data", dataFiltro);
        fechamento.put("totalVendas", totalVendas);
        fechamento.put("quantidadeVendas", pedidosDoDia.size());
        fechamento.put("formasPagamento", vendaseporFormaPagamento);
        fechamento.put("detalhes", detalhes);

        return ResponseEntity.ok(fechamento);
    }

    // =========================
    // TOTALIZADORES DO DIA
    // =========================
    @GetMapping("/totalizadores")
    public ResponseEntity<Map<String, Object>> getTotalizadores(@RequestParam(required = false) String data) {
        LocalDate dataFiltro = (data != null && !data.isEmpty()) ? LocalDate.parse(data) : LocalDate.now();

        LocalDateTime inicioDodia = dataFiltro.atStartOfDay();
        LocalDateTime fimDodia = dataFiltro.atTime(LocalTime.MAX);

        List<Pedido> pedidosDoDia = pedidoRepository.findAll()
                .stream()
                .filter(p -> p.getData() != null && 
                        !p.getData().isBefore(inicioDodia) && 
                        !p.getData().isAfter(fimDodia) &&
                        "FINALIZADO".equals(p.getStatus()))
                .collect(Collectors.toList());

        Double totalVendas = pedidosDoDia.stream()
                .mapToDouble(p -> p.getTotal() != null ? p.getTotal() : 0.0)
                .sum();

        Map<String, Object> totalizadores = new HashMap<>();
        totalizadores.put("data", dataFiltro);
        totalizadores.put("totalVendas", totalVendas);
        totalizadores.put("quantidadeVendas", pedidosDoDia.size());
        totalizadores.put("ticketMedio", pedidosDoDia.size() > 0 ? totalVendas / pedidosDoDia.size() : 0.0);

        return ResponseEntity.ok(totalizadores);
    }
}
