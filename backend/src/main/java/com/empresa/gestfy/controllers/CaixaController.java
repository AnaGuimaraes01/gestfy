package com.empresa.gestfy.controllers;

import com.empresa.gestfy.models.Caixa;
import com.empresa.gestfy.repositories.CaixaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/caixa")
public class CaixaController {

    private final CaixaRepository caixaRepository;

    public CaixaController(CaixaRepository caixaRepository) {
        this.caixaRepository = caixaRepository;
    }

        // =========================
        // ABRIR CAIXA DO DIA
        // =========================
        @PostMapping("/abrir")
        public ResponseEntity<Map<String, Object>> abrirCaixa() {
                LocalDate hoje = LocalDate.now();

                // Verifica se já existe um caixa aberto para hoje
                Optional<Caixa> caixaAberto = caixaRepository.findByDataAndStatus(hoje, "ABERTO");
                if (caixaAberto.isPresent()) {
                        return ResponseEntity.status(HttpStatus.CONFLICT)
                                        .body(Map.of(
                                                        "erro", "Caixa já está aberto para hoje",
                                                        "id", caixaAberto.get().getId()));
                }

                // Cria novo registro de abertura
                Caixa caixa = new Caixa();
                caixa.setTipo("ABERTURA");
                caixa.setData(hoje);
                caixa.setHorarioAbertura(LocalDateTime.now());
                caixa.setStatus("ABERTO");
                caixa.setSaldo(0.0);
                caixa.setDescricao("Abertura de caixa");
                caixa.setObservacoes("Caixa aberto automaticamente");

                Caixa salvo = caixaRepository.save(caixa);

                Map<String, Object> response = new HashMap<>();
                response.put("sucesso", true);
                response.put("mensagem", "Caixa aberto com sucesso");
                response.put("id", salvo.getId());
                response.put("data", salvo.getData());
                response.put("horarioAbertura", salvo.getHorarioAbertura());

                return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        // =========================
        // FECHAR CAIXA DO DIA
        // =========================
        @PostMapping("/fechar")
        public ResponseEntity<Map<String, Object>> fecharCaixa(
                        @RequestParam(required = false) String data,
                        @RequestParam(required = false) String observacoes) {

                LocalDate dataFechamento = (data != null && !data.isEmpty())
                                ? LocalDate.parse(data)
                                : LocalDate.now();

                // Verifica se existe caixa aberto para essa data
                Optional<Caixa> caixaAberto = caixaRepository.findByDataAndStatus(dataFechamento, "ABERTO");
                if (caixaAberto.isEmpty()) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                        .body(Map.of("erro", "Nenhum caixa aberto para fechar nesta data"));
                }

                // Verifica se já foi fechado hoje
                Optional<Caixa> caixaFechado = caixaRepository.findByDataAndTipoAndStatus(
                                dataFechamento, "FECHAMENTO", "FECHADO");
                if (caixaFechado.isPresent()) {
                        return ResponseEntity.status(HttpStatus.CONFLICT)
                                        .body(Map.of("erro", "Caixa já foi fechado para esta data"));
                }

        // ✅ CORRETO: Busca apenas as ENTRADAS registradas no caixa do dia (não os pedidos!)
        List<Caixa> entradas = caixaRepository.findByDataAndTipo(dataFechamento, "ENTRADA");
        
        Double totalVendas = entradas.stream()
                .mapToDouble(c -> c.getSaldo() != null ? c.getSaldo() : 0.0)
                .sum();

        // Cria registro de FECHAMENTO
        Caixa caixaFechamento = new Caixa();
        caixaFechamento.setTipo("FECHAMENTO");
        caixaFechamento.setData(dataFechamento);
        caixaFechamento.setHorarioFechamento(LocalDateTime.now());
        caixaFechamento.setStatus("FECHADO");
        caixaFechamento.setSaldo(totalVendas);
        caixaFechamento.setDescricao("Fechamento de caixa");
        caixaFechamento.setObservacoes(observacoes != null ? observacoes : "Fechamento normal");

        Caixa salvo = caixaRepository.save(caixaFechamento);

        // Atualiza registro de abertura para FECHADO
        Caixa abertura = caixaAberto.get();
        abertura.setStatus("FECHADO");
        abertura.setHorarioFechamento(LocalDateTime.now());
        caixaRepository.save(abertura);

        Map<String, Object> response = new HashMap<>();
        response.put("sucesso", true);
        response.put("mensagem", "Caixa fechado com sucesso");
        response.put("id", salvo.getId());
        response.put("data", salvo.getData());
        response.put("horarioFechamento", salvo.getHorarioFechamento());
        response.put("totalEntradas", totalVendas);
        response.put("quantidadeEntradas", entradas.size());

        return ResponseEntity.ok(response);
    }

    // =========================
    // BUSCAR VENDAS DO DIA
    // =========================
    @GetMapping("/dia")
    public ResponseEntity<Map<String, Object>> obterCaixaDoDia(
            @RequestParam(required = false) String data) {

        LocalDate dataFiltro = (data != null && !data.isEmpty())
                ? LocalDate.parse(data)
                : LocalDate.now();

        // ✅ CORRETO: Busca apenas as ENTRADAS registradas no caixa
        List<Caixa> entradas = caixaRepository.findByDataAndTipo(dataFiltro, "ENTRADA");

        // Calcular totais
        Double totalDia = entradas.stream()
                .mapToDouble(c -> c.getSaldo() != null ? c.getSaldo() : 0.0)
                .sum();

        // Criar lista de registros para tabela
        List<Map<String, Object>> registros = entradas.stream()
                .map(c -> {
                    Map<String, Object> reg = new HashMap<>();
                    reg.put("id", c.getId());
                    reg.put("descricao", c.getDescricao());
                    reg.put("saldo", c.getSaldo() != null ? c.getSaldo() : 0.0);
                    reg.put("data", c.getData());
                    reg.put("hora", c.getHorarioAbertura());
                    reg.put("observacoes", c.getObservacoes());
                    return reg;
                })
                .collect(Collectors.toList());

        // Verifica status do caixa
        Optional<Caixa> caixaAberto = caixaRepository.findByDataAndStatus(dataFiltro, "ABERTO");
        Optional<Caixa> caixaFechado = caixaRepository.findByDataAndTipoAndStatus(
                dataFiltro, "FECHAMENTO", "FECHADO");

        Map<String, Object> response = new HashMap<>();
        response.put("data", dataFiltro);
        response.put("totalDia", totalDia);
        response.put("quantidadeRegistros", entradas.size());
        response.put("registros", registros);
        response.put("caixaAberto", caixaAberto.isPresent());
        response.put("caixaFechado", caixaFechado.isPresent());

        return ResponseEntity.ok(response);
    }

    // =========================
    // RELATÓRIO DE FECHAMENTO DO DIA
    // =========================
    @GetMapping("/relatorio/fechamento")
    public ResponseEntity<Map<String, Object>> fechamentoDia(
            @RequestParam(required = false) String data) {

        LocalDate dataFiltro = (data != null && !data.isEmpty())
                ? LocalDate.parse(data)
                : LocalDate.now();

        // ✅ CORRETO: Busca apenas as ENTRADAS registradas no caixa
        List<Caixa> entradas = caixaRepository.findByDataAndTipo(dataFiltro, "ENTRADA");

        Double totalVendas = entradas.stream()
                .mapToDouble(c -> c.getSaldo() != null ? c.getSaldo() : 0.0)
                .sum();

        // Totalizadores por forma de pagamento (extraído das observações)
        Map<String, Double> vendaseporFormaPagamento = new HashMap<>();
        entradas.forEach(c -> {
            String forma = c.getObservacoes() != null
                    ? c.getObservacoes().replaceFirst("Forma de pagamento: ", "")
                    : "Indefinido";
            vendaseporFormaPagamento.put(forma,
                    vendaseporFormaPagamento.getOrDefault(forma, 0.0) +
                            (c.getSaldo() != null ? c.getSaldo() : 0.0));
        });

        List<Map<String, Object>> detalhes = entradas.stream()
                .map(c -> {
                    Map<String, Object> det = new HashMap<>();
                    det.put("id", c.getId());
                    det.put("descricao", c.getDescricao());
                    det.put("valor", c.getSaldo());
                    det.put("hora", c.getHorarioAbertura());
                    det.put("data", c.getData());
                    return det;
                })
                .collect(Collectors.toList());

        Map<String, Object> fechamento = new HashMap<>();
        fechamento.put("data", dataFiltro);
        fechamento.put("totalVendas", totalVendas);
        fechamento.put("quantidadeVendas", entradas.size());
        fechamento.put("formasPagamento", vendaseporFormaPagamento);
        fechamento.put("detalhes", detalhes);

        return ResponseEntity.ok(fechamento);
    }

    // =========================
    // TOTALIZADORES DO DIA
    // =========================
    @GetMapping("/totalizadores")
    public ResponseEntity<Map<String, Object>> getTotalizadores(
            @RequestParam(required = false) String data) {

        LocalDate dataFiltro = (data != null && !data.isEmpty())
                ? LocalDate.parse(data)
                : LocalDate.now();

        // ✅ CORRETO: Busca apenas as ENTRADAS registradas no caixa
        List<Caixa> entradas = caixaRepository.findByDataAndTipo(dataFiltro, "ENTRADA");

        Double totalVendas = entradas.stream()
                .mapToDouble(c -> c.getSaldo() != null ? c.getSaldo() : 0.0)
                .sum();

        Map<String, Object> totalizadores = new HashMap<>();
        totalizadores.put("data", dataFiltro);
        totalizadores.put("totalVendas", totalVendas);
        totalizadores.put("quantidadeVendas", entradas.size());
        totalizadores.put("ticketMedio", entradas.size() > 0
                ? totalVendas / entradas.size()
                : 0.0);

        return ResponseEntity.ok(totalizadores);
    }
}
