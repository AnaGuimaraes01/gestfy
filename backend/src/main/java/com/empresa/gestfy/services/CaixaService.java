package com.empresa.gestfy.services;

import com.empresa.gestfy.dto.caixa.ProdutoBuscaResponse;
import com.empresa.gestfy.dto.caixa.VendaRequest;
import com.empresa.gestfy.dto.caixa.VendaResponse;
import com.empresa.gestfy.models.Caixa;
import com.empresa.gestfy.models.Produto;
import com.empresa.gestfy.repositories.CaixaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * CaixaService
 * Responsável pela lógica completa do caixa:
 * - Abertura e fechamento
 * - Registros de venda
 * - Cálculos de troco
 * - Consultas de status
 */
@Service
public class CaixaService {

    private final CaixaRepository caixaRepository;
    private final ProdutoService produtoService;
    private final EstoqueService estoqueService;

    public CaixaService(CaixaRepository caixaRepository,
            ProdutoService produtoService,
            EstoqueService estoqueService) {
        this.caixaRepository = caixaRepository;
        this.produtoService = produtoService;
        this.estoqueService = estoqueService;
    }

    // ========================================
    // 1. ABERTURA DO CAIXA
    // ========================================

    /**
     * Abre o caixa do dia.
     * Verifica se já existe um caixa aberto.
     * 
     * @return Map contendo status e detalhes da abertura
     */
    public Map<String, Object> abrirCaixa() {
        LocalDate hoje = LocalDate.now();

        // Verifica se já existe um caixa aberto para hoje
        Optional<Caixa> caixaExistente = caixaRepository.findByDataAndStatus(hoje, "ABERTO");
        if (caixaExistente.isPresent()) {
            return Map.of(
                    "sucesso", false,
                    "erro", "Caixa já está aberto para hoje",
                    "caixaId", caixaExistente.get().getId());
        }

        // Cria novo registro de abertura
        Caixa caixa = new Caixa();
        caixa.setTipo("ABERTURA");
        caixa.setData(hoje);
        caixa.setHorarioAbertura(LocalDateTime.now());
        caixa.setStatus("ABERTO");
        caixa.setSaldo(0.0);
        caixa.setDescricao("Caixa aberto - Início do dia");
        caixa.setObservacoes("Sistema automático");

        Caixa salvo = caixaRepository.save(caixa);

        return Map.of(
                "sucesso", true,
                "mensagem", "Caixa aberto com sucesso!",
                "caixaId", salvo.getId(),
                "data", salvo.getData(),
                "horario", salvo.getHorarioAbertura());
    }

    // ========================================
    // 2. BUSCA DE PRODUTOS
    // ========================================

    /**
     * Busca produtos pelo nome.
     * Usa a lógica centralizada do ProdutoService.
     * 
     * @param nome Nome ou parte do nome
     * @return Lista de produtos encontrados
     */
    public List<ProdutoBuscaResponse> buscarProdutos(String nome) {
        return produtoService.buscarPorNome(nome);
    }

    // ========================================
    // 3. REGISTRO DE VENDA
    // ========================================

    /**
     * Registra uma venda no caixa.
     * Validações:
     * - Produto existe
     * - Estoque suficiente
     * - Valor recebido >= valor total
     * 
     * @param venda VendaRequest com dados da venda
     * @return Map com detalhes da venda ou erro
     */
    public Map<String, Object> registrarVenda(VendaRequest venda) {
        // 1️⃣ Busca o produto
        Optional<Produto> produtoOpt = produtoService.buscarProdutoModelo(venda.produtoId());
        if (produtoOpt.isEmpty()) {
            return Map.of(
                    "sucesso", false,
                    "erro", "Produto não encontrado com ID: " + venda.produtoId());
        }

        Produto produto = produtoOpt.get();

        // 2️⃣ Valida estoque
        if (!produtoService.temEstoqueSuficiente(produto, venda.quantidade())) {
            return Map.of(
                    "sucesso", false,
                    "erro", "Estoque insuficiente",
                    "produtoNome", produto.getNome(),
                    "estoqueDisponivel", produto.getQuantidade() != null ? produto.getQuantidade() : 0,
                    "quantidadeSolicitada", venda.quantidade());
        }

        // 3️⃣ Calcula valores
        Double valorTotal = produto.getPreco() * venda.quantidade();
        Double troco = venda.valorRecebido() - valorTotal;

        // 4️⃣ Valida valor recebido
        if (venda.valorRecebido() < valorTotal) {
            return Map.of(
                    "sucesso", false,
                    "erro", "Valor recebido é insuficiente",
                    "valorTotal", valorTotal,
                    "valorRecebido", venda.valorRecebido(),
                    "falta", valorTotal - venda.valorRecebido());
        }

        // 5️⃣ Atualiza estoque do produto
        Integer novaQuantidade = produto.getQuantidade() - venda.quantidade();
        produtoService.atualizarEstoque(produto, novaQuantidade);

        // 6️⃣ Registra movimento de saída no estoque
        estoqueService.registrarMovimento(produto.getId(), "SAIDA", venda.quantidade());

        // 7️⃣ Registra a venda no caixa
        Caixa vendaRegistro = new Caixa();
        vendaRegistro.setTipo("ENTRADA");
        vendaRegistro.setData(LocalDate.now());
        vendaRegistro.setHorarioAbertura(LocalDateTime.now());
        vendaRegistro.setStatus("ABERTO");
        vendaRegistro.setSaldo(valorTotal);
        vendaRegistro.setDescricao(String.format(
                "Venda: %s (Qtd: %d)",
                produto.getNome(),
                venda.quantidade()));
        vendaRegistro.setObservacoes(String.format(
                "Preço unitário: R$ %.2f | Valor pago: R$ %.2f | Troco: R$ %.2f",
                produto.getPreco(),
                venda.valorRecebido(),
                troco));

        Caixa vendaSalva = caixaRepository.save(vendaRegistro);

        // 8️⃣ Retorna resposta com detalhes da venda
        VendaResponse vendaResponse = new VendaResponse(
                vendaSalva.getId(),
                produto.getNome(),
                venda.quantidade(),
                produto.getPreco(),
                valorTotal,
                venda.valorRecebido(),
                troco);

        return Map.of(
                "sucesso", true,
                "venda", vendaResponse,
                "estoqueAtualizado", novaQuantidade);
    }

    // ========================================
    // 4. FECHAMENTO DO CAIXA
    // ========================================

    /**
     * Fecha o caixa do dia.
     * Calcula totalizadores e registra fechamento.
     * 
     * @return Map com detalhes do fechamento ou erro
     */
    public Map<String, Object> fecharCaixa() {
        LocalDate hoje = LocalDate.now();

        // Verifica se existe caixa aberto
        Optional<Caixa> caixaAberto = caixaRepository.findByDataAndStatus(hoje, "ABERTO");
        if (caixaAberto.isEmpty()) {
            return Map.of(
                    "sucesso", false,
                    "erro", "Nenhum caixa aberto para fechar hoje");
        }

        // Verifica se já foi fechado
        Optional<Caixa> jaFechado = caixaRepository.findByDataAndTipoAndStatus(
                hoje, "FECHAMENTO", "FECHADO");
        if (jaFechado.isPresent()) {
            return Map.of(
                    "sucesso", false,
                    "erro", "Caixa já foi fechado hoje");
        }

        // Busca todas as vendas do dia
        List<Caixa> vendas = caixaRepository.findByDataAndTipo(hoje, "ENTRADA");

        // Calcula total
        Double totalVendas = vendas.stream()
                .mapToDouble(c -> c.getSaldo() != null ? c.getSaldo() : 0.0)
                .sum();

        // Registra fechamento
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
                totalVendas));

        Caixa fechamentoSalvo = caixaRepository.save(fechamento);

        // Atualiza status do caixa aberto
        Caixa caixa = caixaAberto.get();
        caixa.setStatus("FECHADO");
        caixa.setHorarioFechamento(LocalDateTime.now());
        caixaRepository.save(caixa);

        return Map.of(
                "sucesso", true,
                "mensagem", "Caixa fechado com sucesso!",
                "totalVendas", vendas.size(),
                "totalArrecadado", totalVendas,
                "data", hoje,
                "horarioFechamento", fechamento.getHorarioFechamento());
    }

    // ========================================
    // 5. CONSULTAS
    // ========================================

    /**
     * Lista todas as vendas do dia
     * 
     * @return Map com lista de vendas e totalizadores
     */
    public Map<String, Object> listarVendasDoDia() {
        LocalDate hoje = LocalDate.now();

        List<Caixa> vendas = caixaRepository.findByDataAndTipo(hoje, "ENTRADA");

        Double total = vendas.stream()
                .mapToDouble(c -> c.getSaldo() != null ? c.getSaldo() : 0.0)
                .sum();

        return Map.of(
                "sucesso", true,
                "data", hoje,
                "totalVendas", vendas.size(),
                "totalArrecadado", total,
                "vendas", vendas);
    }

    /**
     * Obtém o status atual do caixa
     * 
     * @return Map com status (aberto/fechado) e detalhes
     */
    public Map<String, Object> obterStatus() {
        LocalDate hoje = LocalDate.now();

        Optional<Caixa> caixaAberto = caixaRepository.findByDataAndStatus(hoje, "ABERTO");

        if (caixaAberto.isPresent()) {
            Caixa caixa = caixaAberto.get();
            List<Caixa> vendas = caixaRepository.findByDataAndTipo(hoje, "ENTRADA");
            Double totalVendas = vendas.stream()
                    .mapToDouble(c -> c.getSaldo() != null ? c.getSaldo() : 0.0)
                    .sum();

            return Map.of(
                    "aberto", true,
                    "caixaId", caixa.getId(),
                    "horarioAbertura", caixa.getHorarioAbertura(),
                    "totalVendas", vendas.size(),
                    "totalArrecadado", totalVendas,
                    "data", hoje);
        }

        return Map.of(
                "aberto", false,
                "mensagem", "Caixa fechado para hoje. Abra o caixa para começar.",
                "data", hoje);
    }
}
