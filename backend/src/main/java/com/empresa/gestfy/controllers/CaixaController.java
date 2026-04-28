package com.empresa.gestfy.controllers;

import com.empresa.gestfy.models.Caixa;
import com.empresa.gestfy.models.Estoque;
import com.empresa.gestfy.models.Produto;
import com.empresa.gestfy.repositories.CaixaRepository;
import com.empresa.gestfy.repositories.EstoqueRepository;
import com.empresa.gestfy.repositories.ProdutoRepository;
import com.empresa.gestfy.dto.caixa.ProdutoBuscaResponse;
import com.empresa.gestfy.dto.caixa.VendaRequest;
import com.empresa.gestfy.dto.caixa.VendaResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

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
    private final ProdutoRepository produtoRepository;
    private final EstoqueRepository estoqueRepository;

    public CaixaController(CaixaRepository caixaRepository, 
                          ProdutoRepository produtoRepository,
                          EstoqueRepository estoqueRepository) {
        this.caixaRepository = caixaRepository;
        this.produtoRepository = produtoRepository;
        this.estoqueRepository = estoqueRepository;
    }

    // ========================================
    // 1. ABERTURA DO CAIXA
    // ========================================
    /**
     * Abre o caixa do dia.
     * Verifica se já existe um caixa aberto para hoje.
     */
    @PostMapping("/abrir")
    public ResponseEntity<Map<String, Object>> abrirCaixa() {
        LocalDate hoje = LocalDate.now();

        // ✅ Verifica se já existe um caixa aberto para hoje
        Optional<Caixa> caixaExistente = caixaRepository.findByDataAndStatus(hoje, "ABERTO");
        if (caixaExistente.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "erro", "Caixa já está aberto para hoje",
                            "caixaId", caixaExistente.get().getId()
                    ));
        }

        // ✅ Cria novo registro de abertura
        Caixa caixa = new Caixa();
        caixa.setTipo("ABERTURA");
        caixa.setData(hoje);
        caixa.setHorarioAbertura(LocalDateTime.now());
        caixa.setStatus("ABERTO");
        caixa.setSaldo(0.0);
        caixa.setDescricao("Caixa aberto - Início do dia");
        caixa.setObservacoes("Sistema automático");

        Caixa salvo = caixaRepository.save(caixa);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "sucesso", true,
                        "mensagem", "Caixa aberto com sucesso!",
                        "caixaId", salvo.getId(),
                        "data", salvo.getData(),
                        "horario", salvo.getHorarioAbertura()
                ));
    }

    // ========================================
    // 2. BUSCAR PRODUTOS POR NOME (PARCIAL)
    // ========================================
    /**
     * Busca produtos pelo nome (busca parcial, case-insensitive).
     * Retorna lista com ID, nome, preço e estoque disponível.
     * 
     * Exemplo: GET /api/caixa/buscar-produto?nome=sorvete
     */
    @GetMapping("/buscar-produto")
    public ResponseEntity<Map<String, Object>> buscarProduto(
            @RequestParam(required = true) String nome) {

        // ✅ Valida se o nome foi informado
        if (nome == null || nome.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "erro", "Por favor, informe o nome do produto para buscar"
                    ));
        }

        // ✅ Busca produtos com nome parcial (case-insensitive)
        List<Produto> produtos = produtoRepository.findByNomeContainingIgnoreCase(nome.trim());

        // ✅ Se não encontrou nenhum produto
        if (produtos.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "erro", "Nenhum produto encontrado com o nome: " + nome,
                            "produtos", List.of()
                    ));
        }

        // ✅ Converte produtos encontrados em DTOs de resposta
        List<ProdutoBuscaResponse> resposta = produtos.stream()
                .map(p -> new ProdutoBuscaResponse(
                        p.getId(),
                        p.getNome(),
                        p.getPreco(),
                        p.getQuantidade() != null ? p.getQuantidade() : 0
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(Map.of(
                "sucesso", true,
                "total", resposta.size(),
                "produtos", resposta
        ));
    }

    // ========================================
    // 3. REGISTRAR VENDA
    // ========================================
    /**
     * Registra uma venda no caixa.
     * 
     * Processa:
     * - Validação do produto e estoque
     * - Cálculo do valor total
     * - Cálculo do troco
     * - Atualização do estoque
     * - Registro da venda no caixa
     * 
     * POST body:
     * {
     *   "produtoId": 1,
     *   "quantidade": 2,
     *   "valorRecebido": 50.00
     * }
     */
    @PostMapping("/vender")
    public ResponseEntity<Map<String, Object>> registrarVenda(@Valid @RequestBody VendaRequest venda) {
        // ✅ Busca o produto pelo ID
        Optional<Produto> produtoOpt = produtoRepository.findById(venda.produtoId());
        if (produtoOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "erro", "Produto não encontrado com ID: " + venda.produtoId()
                    ));
        }

        Produto produto = produtoOpt.get();

        // ✅ Valida se há estoque suficiente
        if (produto.getQuantidade() == null || produto.getQuantidade() < venda.quantidade()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "erro", "Estoque insuficiente",
                            "produtoNome", produto.getNome(),
                            "estoqueDisponivel", produto.getQuantidade() != null ? produto.getQuantidade() : 0,
                            "quantidadeSolicitada", venda.quantidade()
                    ));
        }

        // ✅ Calcula o valor total da venda
        Double valorTotal = produto.getPreco() * venda.quantidade();

        // ✅ Calcula o troco
        Double troco = venda.valorRecebido() - valorTotal;

        // ✅ Valida se o valor recebido é suficiente
        if (venda.valorRecebido() < valorTotal) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "erro", "Valor recebido é insuficiente",
                            "valorTotal", valorTotal,
                            "valorRecebido", venda.valorRecebido(),
                            "falta", valorTotal - venda.valorRecebido()
                    ));
        }

        // ✅ ATUALIZA O ESTOQUE DO PRODUTO
        Integer novaQuantidade = produto.getQuantidade() - venda.quantidade();
        produto.setQuantidade(novaQuantidade);
        produtoRepository.save(produto);

        // ✅ REGISTRA MOVIMENTO DE SAÍDA NO ESTOQUE
        Estoque movimentoEstoque = new Estoque();
        movimentoEstoque.setProdutoId(produto.getId());
        movimentoEstoque.setTipoMovimento("SAIDA");
        movimentoEstoque.setDataMovimento(LocalDateTime.now());
        movimentoEstoque.setQuantidade(venda.quantidade());
        estoqueRepository.save(movimentoEstoque);

        // ✅ REGISTRA A VENDA NO CAIXA (como ENTRADA)
        Caixa vendaRegistro = new Caixa();
        vendaRegistro.setTipo("ENTRADA");
        vendaRegistro.setData(LocalDate.now());
        vendaRegistro.setHorarioAbertura(LocalDateTime.now());
        vendaRegistro.setStatus("ABERTO");
        vendaRegistro.setSaldo(valorTotal);
        vendaRegistro.setDescricao(String.format(
                "Venda: %s (Qtd: %d)",
                produto.getNome(),
                venda.quantidade()
        ));
        vendaRegistro.setObservacoes(String.format(
                "Preço unitário: R$ %.2f | Valor pago: R$ %.2f | Troco: R$ %.2f",
                produto.getPreco(),
                venda.valorRecebido(),
                troco
        ));

        Caixa vendaSalva = caixaRepository.save(vendaRegistro);

        // ✅ Retorna resposta com os detalhes da venda
        VendaResponse vendaResponse = new VendaResponse(
                vendaSalva.getId(),
                produto.getNome(),
                venda.quantidade(),
                produto.getPreco(),
                valorTotal,
                venda.valorRecebido(),
                troco
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "sucesso", true,
                        "venda", vendaResponse,
                        "estoqueAtualizado", novaQuantidade
                ));
    }

    // ========================================
    // 4. FECHAR CAIXA DO DIA
    // ========================================
    /**
     * Fecha o caixa do dia.
     * Calcula o total de vendas e registra o fechamento.
     */
    @PostMapping("/fechar")
    public ResponseEntity<Map<String, Object>> fecharCaixa() {
        LocalDate hoje = LocalDate.now();

        // ✅ Verifica se existe caixa aberto para hoje
        Optional<Caixa> caixaAberto = caixaRepository.findByDataAndStatus(hoje, "ABERTO");
        if (caixaAberto.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "erro", "Nenhum caixa aberto para fechar hoje"
                    ));
        }

        // ✅ Verifica se já foi fechado hoje
        Optional<Caixa> jaFechado = caixaRepository.findByDataAndTipoAndStatus(
                hoje, "FECHAMENTO", "FECHADO");
        if (jaFechado.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "erro", "Caixa já foi fechado hoje"
                    ));
        }

        // ✅ Busca todas as ENTRADAS (vendas) do dia
        List<Caixa> vendas = caixaRepository.findByDataAndTipo(hoje, "ENTRADA");

        // ✅ Calcula total de vendas
        Double totalVendas = vendas.stream()
                .mapToDouble(c -> c.getSaldo() != null ? c.getSaldo() : 0.0)
                .sum();

        // ✅ Registra o FECHAMENTO do caixa
        Caixa fechamento = new Caixa();
        fechamento.setTipo("FECHAMENTO");
        fechamento.setData(hoje);
        fechamento.setHorarioFechamento(LocalDateTime.now());
        fechamento.setStatus("FECHADO");
        fechamento.setSaldo(totalVendas);
        fechamento.setDescricao("Fechamento de caixa do dia");
        fechamento.setObservacoes(String.format(
                "Total de vendas: %d | Total arrecadado: R$ %.2f",
                vendas.size(),
                totalVendas
        ));

        Caixa fechamentoSalvo = caixaRepository.save(fechamento);

        // ✅ Atualiza status do caixa aberto para FECHADO
        Caixa caixa = caixaAberto.get();
        caixa.setStatus("FECHADO");
        caixa.setHorarioFechamento(LocalDateTime.now());
        caixaRepository.save(caixa);

        return ResponseEntity.ok(Map.of(
                "sucesso", true,
                "mensagem", "Caixa fechado com sucesso!",
                "totalVendas", vendas.size(),
                "totalArrecadado", totalVendas,
                "data", hoje,
                "horarioFechamento", fechamento.getHorarioFechamento()
        ));
    }

    // ========================================
    // 5. LISTAR VENDAS DO DIA
    // ========================================
    /**
     * Lista todas as vendas (ENTRADA) registradas no dia.
     */
    @GetMapping("/vendas-do-dia")
    public ResponseEntity<Map<String, Object>> listarVendasDoDia() {
        LocalDate hoje = LocalDate.now();

        // ✅ Busca todas as entradas do dia
        List<Caixa> vendas = caixaRepository.findByDataAndTipo(hoje, "ENTRADA");

        // ✅ Calcula total
        Double total = vendas.stream()
                .mapToDouble(c -> c.getSaldo() != null ? c.getSaldo() : 0.0)
                .sum();

        return ResponseEntity.ok(Map.of(
                "sucesso", true,
                "data", hoje,
                "totalVendas", vendas.size(),
                "totalArrecadado", total,
                "vendas", vendas
        ));
    }

    // ========================================
    // 6. OBTER STATUS DO CAIXA
    // ========================================
    /**
     * Verifica o status atual do caixa (se está aberto ou fechado).
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> obterStatus() {
        LocalDate hoje = LocalDate.now();

        Optional<Caixa> caixaAberto = caixaRepository.findByDataAndStatus(hoje, "ABERTO");

        if (caixaAberto.isPresent()) {
            Caixa caixa = caixaAberto.get();
            List<Caixa> vendas = caixaRepository.findByDataAndTipo(hoje, "ENTRADA");
            Double totalVendas = vendas.stream()
                    .mapToDouble(c -> c.getSaldo() != null ? c.getSaldo() : 0.0)
                    .sum();

            return ResponseEntity.ok(Map.of(
                    "aberto", true,
                    "caixaId", caixa.getId(),
                    "horarioAbertura", caixa.getHorarioAbertura(),
                    "totalVendas", vendas.size(),
                    "totalArrecadado", totalVendas,
                    "data", hoje
            ));
        }

        return ResponseEntity.ok(Map.of(
                "aberto", false,
                "mensagem", "Caixa fechado para hoje. Abra o caixa para começar.",
                "data", hoje
        ));
    }
}
