package com.empresa.gestfy.controllers;

import com.empresa.gestfy.dto.caixa.CaixaDTO;
import com.empresa.gestfy.dto.caixa.CaixaRequest;
import com.empresa.gestfy.models.Caixa;
import com.empresa.gestfy.repositories.CaixaRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    // REGISTRAR VENDA NO CAIXA (automático quando pedido finaliza)
    // =========================
    @PostMapping
    public ResponseEntity<CaixaDTO> registrarVenda(@RequestBody @Valid CaixaRequest request) {
        Caixa caixa = new Caixa();
        caixa.setSaldo(request.saldo());
        caixa.setDescricao(request.descricao());
        caixa.setData(LocalDate.now());

        caixa = caixaRepository.save(caixa);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToDTO(caixa));
    }

    // =========================
    // LISTAR TODOS OS REGISTROS
    // =========================
    @GetMapping
    public ResponseEntity<List<CaixaDTO>> listarTodos() {
        List<CaixaDTO> registros = caixaRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(registros);
    }

    // =========================
    // BUSCAR REGISTROS DO DIA
    // =========================
    @GetMapping("/dia")
    public ResponseEntity<Map<String, Object>> obterCaixaDoDia(@RequestParam(required = false) String data) {
        LocalDate dataFiltro = (data != null && !data.isEmpty()) ? LocalDate.parse(data) : LocalDate.now();

        List<Caixa> registrosDoDia = caixaRepository.findByData(dataFiltro);

        Double totalDia = registrosDoDia.stream()
                .mapToDouble(Caixa::getSaldo)
                .sum();

        Map<String, Object> response = new HashMap<>();
        response.put("data", dataFiltro);
        response.put("totalDia", totalDia);
        response.put("quantidadeRegistros", registrosDoDia.size());
        response.put("registros", registrosDoDia.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList()));

        return ResponseEntity.ok(response);
    }

    // =========================
    // BUSCAR POR ID
    // =========================
    @GetMapping("/{id}")
    public ResponseEntity<CaixaDTO> buscarPorId(@PathVariable Long id) {
        return caixaRepository.findById(id)
                .map(c -> ResponseEntity.ok(mapToDTO(c)))
                .orElse(ResponseEntity.notFound().build());
    }

    // =========================
    // ATUALIZAR REGISTRO
    // =========================
    @PutMapping("/{id}")
    public ResponseEntity<CaixaDTO> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid CaixaRequest request) {

        Caixa caixa = caixaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro de caixa não encontrado: ID " + id));

        caixa.setSaldo(request.saldo());
        caixa.setDescricao(request.descricao());

        caixa = caixaRepository.save(caixa);
        return ResponseEntity.ok(mapToDTO(caixa));
    }

    // =========================
    // DELETAR REGISTRO
    // =========================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (!caixaRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        caixaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // =========================
    // FECHAMENTO DO DIA
    // =========================
    @GetMapping("/relatorio/fechamento")
    public ResponseEntity<Map<String, Object>> fechamentoDia(@RequestParam(required = false) String data) {
        LocalDate dataFiltro = (data != null && !data.isEmpty()) ? LocalDate.parse(data) : LocalDate.now();

        List<Caixa> registrosDoDia = caixaRepository.findByData(dataFiltro);

        Double totalEntradas = registrosDoDia.stream()
                .filter(r -> r.getSaldo() > 0)
                .mapToDouble(Caixa::getSaldo)
                .sum();

        Double totalSaidas = registrosDoDia.stream()
                .filter(r -> r.getSaldo() < 0)
                .mapToDouble(Caixa::getSaldo)
                .sum();

        Double saldoLiquido = totalEntradas + totalSaidas;

        Map<String, Object> fechamento = new HashMap<>();
        fechamento.put("data", dataFiltro);
        fechamento.put("totalEntradas", totalEntradas);
        fechamento.put("totalSaidas", Math.abs(totalSaidas));
        fechamento.put("saldoLiquido", saldoLiquido);
        fechamento.put("quantidadeTransacoes", registrosDoDia.size());
        fechamento.put("detalhes", registrosDoDia.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList()));

        return ResponseEntity.ok(fechamento);
    }

    // =========================
    // CONVERTER PARA DTO
    // =========================
    private CaixaDTO mapToDTO(Caixa caixa) {
        return new CaixaDTO(
                caixa.getId(),
                caixa.getSaldo(),
                caixa.getDescricao(),
                caixa.getData()
        );
    }
}
