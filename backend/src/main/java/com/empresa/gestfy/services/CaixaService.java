package com.empresa.gestfy.services;

import com.empresa.gestfy.config.DataHoraBrasil;
import com.empresa.gestfy.dto.caixa.ProdutoBuscaResponse;
import com.empresa.gestfy.dto.caixa.VendaRequest;
import com.empresa.gestfy.dto.caixa.VendaResponse;
import com.empresa.gestfy.dto.caixa.VendaAgrupadaRequest;
import com.empresa.gestfy.dto.caixa.VendaAgrupadaResponse;
import com.empresa.gestfy.dto.caixa.ItemVendaRequest;
import com.empresa.gestfy.dto.caixa.ItemVendaResponse;
import com.empresa.gestfy.models.Caixa;
import com.empresa.gestfy.models.Produto;
import com.empresa.gestfy.repositories.CaixaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                Optional<Caixa> caixaExistente = caixaRepository.findByDataAndTipoAndStatus(hoje, "ABERTURA", "ABERTO");
                if (caixaExistente.isPresent()) {
                        Caixa caixa = caixaExistente.get();
                        // Caixa já aberto - retornar com sucesso (recarregar interface)
                        Map<String, Object> resultado = new java.util.HashMap<>();
                        resultado.put("sucesso", true);
                        resultado.put("mensagem", "Caixa já está aberto para hoje");
                        resultado.put("caixaId", caixa.getId());
                        resultado.put("jaAberto", true);
                        resultado.put("data", caixa.getData().toString());
                        resultado.put("horario", caixa.getDataAbertura() != null ? caixa.getDataAbertura().toString() : "");
                        return resultado;
                }

                // Cria novo registro de abertura
                Caixa caixa = new Caixa();
                caixa.setTipo("ABERTURA");
                caixa.setData(hoje);
                caixa.setDataAbertura(DataHoraBrasil.agora());
                caixa.setHorarioAbertura(DataHoraBrasil.agora()); // Mantém por compatibilidade
                caixa.setStatus("ABERTO");
                caixa.setValorInicial(0.0); // Garantir que nunca seja null
                caixa.setSaldo(0.0);
                caixa.setDescricao("Caixa aberto - Início do dia");
                caixa.setObservacoes("Sistema automático");

                Caixa salvo = caixaRepository.save(caixa);

                Map<String, Object> resultado = new java.util.HashMap<>();
                resultado.put("sucesso", true);
                resultado.put("mensagem", "Caixa aberto com sucesso!");
                resultado.put("caixaId", salvo.getId());
                resultado.put("jaAberto", false);
                resultado.put("data", salvo.getData().toString());
                resultado.put("horario", salvo.getDataAbertura() != null ? salvo.getDataAbertura().toString() : "");
                return resultado;
        }

        // 2. BUSCA DE PRODUTOS

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

        // 3. REGISTRO DE VENDA

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
        @Transactional
        public Map<String, Object> registrarVenda(VendaRequest venda) {
                try {
                        // Busca o produto
                        Optional<Produto> produtoOpt = produtoService.buscarProdutoModelo(venda.produtoId());
                        if (produtoOpt.isEmpty()) {
                                Map<String, Object> erro = new java.util.HashMap<>();
                                erro.put("sucesso", false);
                                erro.put("erro", "Produto não encontrado com ID: " + venda.produtoId());
                                return erro;
                        }

                        Produto produto = produtoOpt.get();

                        // Valida estoque
                        if (!produtoService.temEstoqueSuficiente(produto, venda.quantidade())) {
                                Map<String, Object> erro = new java.util.HashMap<>();
                                erro.put("sucesso", false);
                                erro.put("erro", "Estoque insuficiente");
                                erro.put("produtoNome", produto.getNome());
                                erro.put("estoqueDisponivel", produto.getQuantidade() != null ? produto.getQuantidade() : 0);
                                erro.put("quantidadeSolicitada", venda.quantidade());
                                return erro;
                        }

                        // Calcula valores
                        Double valorTotal = produto.getPreco() * venda.quantidade();
                        Double troco = venda.valorRecebido() - valorTotal;

                        // Valida valor recebido
                        if (venda.valorRecebido() < valorTotal) {
                                Map<String, Object> erro = new java.util.HashMap<>();
                                erro.put("sucesso", false);
                                erro.put("erro", "Valor recebido é insuficiente");
                                erro.put("valorTotal", valorTotal);
                                erro.put("valorRecebido", venda.valorRecebido());
                                erro.put("falta", valorTotal - venda.valorRecebido());
                                return erro;
                        }

                        // Atualiza estoque do produto
                        Integer novaQuantidade = produto.getQuantidade() - venda.quantidade();
                        produtoService.atualizarEstoque(produto, novaQuantidade);

                        // Registra movimento de saída no estoque
                        estoqueService.registrarMovimento(produto.getId(), "SAIDA", venda.quantidade());

                        //Registra a venda no caixa
                        Caixa vendaRegistro = new Caixa();
                        vendaRegistro.setTipo("ENTRADA");
                        vendaRegistro.setData(LocalDate.now());
                        vendaRegistro.setDataAbertura(DataHoraBrasil.agora());
                        vendaRegistro.setHorarioAbertura(DataHoraBrasil.agora());
                        vendaRegistro.setStatus("CONCLUIDO");
                        vendaRegistro.setValorInicial(0.0);
                        vendaRegistro.setSaldo(valorTotal);
                        vendaRegistro.setDescricao(String.format("Venda: %s (Qtd: %d)", produto.getNome(), venda.quantidade()));
                        vendaRegistro.setObservacoes(String.format("Preço unitário: R$ %.2f | Valor pago: R$ %.2f | Troco: R$ %.2f", produto.getPreco(), venda.valorRecebido(), troco));

                        Caixa vendaSalva = caixaRepository.save(vendaRegistro);

                        // Retorna resposta com detalhes da venda
                        VendaResponse vendaResponse = new VendaResponse(vendaSalva.getId(), produto.getNome(), venda.quantidade(), produto.getPreco(), valorTotal, venda.valorRecebido(), troco);

                        Map<String, Object> sucesso = new java.util.HashMap<>();
                        sucesso.put("sucesso", true);
                        sucesso.put("venda", vendaResponse);
                        sucesso.put("estoqueAtualizado", novaQuantidade);
                        return sucesso;
                } catch (Exception e) {
                        e.printStackTrace();
                        Map<String, Object> erro = new java.util.HashMap<>();
                        erro.put("sucesso", false);
                        erro.put("erro", "Erro ao registrar venda: " + e.getMessage());
                        return erro;
                }
        }

        // 3.1 REGISTRO DE VENDA AGRUPADA

        /**
         * Registra uma venda agrupada com múltiplos itens.
         * Todos os itens são agrupados em uma única entrada no caixa.
         * 
         * @param venda VendaAgrupadaRequest com lista de itens e valor recebido
         * @return Map com detalhes da venda agrupada ou erro
         */
        @Transactional
        public Map<String, Object> registrarVendaAgrupada(VendaAgrupadaRequest venda) {
                try {
                        // Validar lista de itens
                        if (venda.itens() == null || venda.itens().isEmpty()) {
                                Map<String, Object> erro = new java.util.HashMap<>();
                                erro.put("sucesso", false);
                                erro.put("erro", "A venda deve conter pelo menos um item");
                                return erro;
                        }

                        // Processar cada item e validar
                        Double valorTotal = 0.0;
                        java.util.List<ItemVendaResponse> itensResposta = new java.util.ArrayList<>();
                        
                        for (ItemVendaRequest itemRequest : venda.itens()) {
                                // Busca o produto
                                Optional<Produto> produtoOpt = produtoService.buscarProdutoModelo(itemRequest.produtoId());
                                if (produtoOpt.isEmpty()) {
                                        Map<String, Object> erro = new java.util.HashMap<>();
                                        erro.put("sucesso", false);
                                        erro.put("erro", "Produto não encontrado com ID: " + itemRequest.produtoId());
                                        return erro;
                                }

                                Produto produto = produtoOpt.get();

                                // Valida estoque
                                if (!produtoService.temEstoqueSuficiente(produto, itemRequest.quantidade())) {
                                        Map<String, Object> erro = new java.util.HashMap<>();
                                        erro.put("sucesso", false);
                                        erro.put("erro", "Estoque insuficiente para: " + produto.getNome());
                                        erro.put("produtoNome", produto.getNome());
                                        erro.put("estoqueDisponivel", produto.getQuantidade() != null ? produto.getQuantidade() : 0);
                                        erro.put("quantidadeSolicitada", itemRequest.quantidade());
                                        return erro;
                                }

                                // Calcula subtotal do item
                                Double subtotal = produto.getPreco() * itemRequest.quantidade();
                                valorTotal += subtotal;

                                // Adiciona à resposta
                                itensResposta.add(new ItemVendaResponse(
                                        produto.getId(),
                                        produto.getNome(),
                                        itemRequest.quantidade(),
                                        produto.getPreco(),
                                        subtotal
                                ));

                                // Atualiza estoque do produto
                                Integer novaQuantidade = produto.getQuantidade() - itemRequest.quantidade();
                                produtoService.atualizarEstoque(produto, novaQuantidade);

                                // Registra movimento de saída no estoque
                                estoqueService.registrarMovimento(produto.getId(), "SAIDA", itemRequest.quantidade());
                        }

                        // Valida valor recebido
                        if (venda.valorRecebido() < valorTotal) {
                                Map<String, Object> erro = new java.util.HashMap<>();
                                erro.put("sucesso", false);
                                erro.put("erro", "Valor recebido é insuficiente");
                                erro.put("valorTotal", valorTotal);
                                erro.put("valorRecebido", venda.valorRecebido());
                                erro.put("falta", valorTotal - venda.valorRecebido());
                                return erro;
                        }

                        Double troco = venda.valorRecebido() - valorTotal;

                        // Monta descrição detalhada da venda agrupada
                        StringBuilder descricaoBuilder = new StringBuilder("Venda: ");
                        StringBuilder observacoesBuilder = new StringBuilder();
                        
                        for (int i = 0; i < itensResposta.size(); i++) {
                                ItemVendaResponse item = itensResposta.get(i);
                                if (i > 0) {
                                        descricaoBuilder.append(" | ");
                                        observacoesBuilder.append(" | ");
                                }
                                descricaoBuilder.append(item.getQuantidade()).append("x ").append(item.getNomeProduto());
                                observacoesBuilder.append(item.getQuantidade()).append("x ")
                                        .append(item.getNomeProduto()).append(" (R$ ")
                                        .append(String.format("%.2f", item.getSubtotal())).append(")");
                        }

                        observacoesBuilder.append(" | Valor total: R$ ").append(String.format("%.2f", valorTotal))
                                .append(" | Valor pago: R$ ").append(String.format("%.2f", venda.valorRecebido()))
                                .append(" | Troco: R$ ").append(String.format("%.2f", troco));

                        // Registra a venda agrupada como uma única entrada no caixa
                        Caixa vendaRegistro = new Caixa();
                        vendaRegistro.setTipo("ENTRADA");
                        vendaRegistro.setData(LocalDate.now());
                        vendaRegistro.setDataAbertura(DataHoraBrasil.agora());
                        vendaRegistro.setHorarioAbertura(DataHoraBrasil.agora());
                        vendaRegistro.setStatus("CONCLUIDO");
                        vendaRegistro.setValorInicial(0.0);
                        vendaRegistro.setSaldo(valorTotal);
                        vendaRegistro.setDescricao(descricaoBuilder.toString());
                        vendaRegistro.setObservacoes(observacoesBuilder.toString());

                        Caixa vendaSalva = caixaRepository.save(vendaRegistro);

                        // Retorna resposta com detalhes da venda agrupada
                        VendaAgrupadaResponse vendaResponse = new VendaAgrupadaResponse(
                                vendaSalva.getId(),
                                itensResposta,
                                valorTotal,
                                venda.valorRecebido(),
                                troco,
                                venda.itens().size()
                        );

                        Map<String, Object> sucesso = new java.util.HashMap<>();
                        sucesso.put("sucesso", true);
                        sucesso.put("venda", vendaResponse);
                        return sucesso;
                } catch (Exception e) {
                        e.printStackTrace();
                        Map<String, Object> erro = new java.util.HashMap<>();
                        erro.put("sucesso", false);
                        erro.put("erro", "Erro ao registrar venda agrupada: " + e.getMessage());
                        return erro;
                }
        }

        // 4. FECHAMENTO DO CAIXA

        /**
         * Fecha o caixa do dia.
         * Calcula totalizadores e registra fechamento.
         * 
         * @return Map com detalhes do fechamento ou erro
         */
        public Map<String, Object> fecharCaixa() {
                try {
                        LocalDate hoje = LocalDate.now();

                        // Verifica se existe caixa aberto
                        Optional<Caixa> caixaAberto = caixaRepository.findByDataAndTipoAndStatus(hoje, "ABERTURA", "ABERTO");
                        if (caixaAberto.isEmpty()) {
                                Map<String, Object> erro = new java.util.HashMap<>();
                                erro.put("sucesso", false);
                                erro.put("erro", "Nenhum caixa aberto para fechar hoje");
                                return erro;
                        }

                        // Verifica se já foi fechado
                        Optional<Caixa> jaFechado = caixaRepository.findByDataAndTipoAndStatus(hoje, "FECHAMENTO", "FECHADO");
                        if (jaFechado.isPresent()) {
                                Map<String, Object> erro = new java.util.HashMap<>();
                                erro.put("sucesso", false);
                                erro.put("erro", "Caixa já foi fechado hoje");
                                return erro;
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
                        fechamento.setDataFechamento(DataHoraBrasil.agora());
                        fechamento.setHorarioFechamento(DataHoraBrasil.agora());
                        fechamento.setStatus("FECHADO");
                        fechamento.setValorInicial(0.0);
                        fechamento.setValorFinal(totalVendas);
                        fechamento.setSaldo(totalVendas);
                        fechamento.setDescricao("Fechamento de caixa do dia");
                        fechamento.setObservacoes(String.format("Total de vendas: %d | Total arrecadado: R$ %.2f", vendas.size(), totalVendas));

                        Caixa fechamentoSalvo = caixaRepository.save(fechamento);

                        // Atualiza status do caixa aberto
                        Caixa caixa = caixaAberto.get();
                        caixa.setStatus("FECHADO");
                        caixa.setDataFechamento(DataHoraBrasil.agora());
                        caixa.setHorarioFechamento(DataHoraBrasil.agora());
                        caixa.setValorFinal(totalVendas);
                        caixaRepository.save(caixa);

                        Map<String, Object> resultado = new java.util.HashMap<>();
                        resultado.put("sucesso", true);
                        resultado.put("mensagem", "Caixa fechado com sucesso!");
                        resultado.put("totalVendas", vendas.size());
                        resultado.put("totalArrecadado", totalVendas);
                        resultado.put("data", hoje.toString());
                        resultado.put("horarioFechamento", fechamentoSalvo.getDataFechamento() != null ? fechamentoSalvo.getDataFechamento().toString() : "");
                        return resultado;
                } catch (Exception e) {
                        e.printStackTrace();
                        Map<String, Object> erro = new java.util.HashMap<>();
                        erro.put("sucesso", false);
                        erro.put("erro", "Erro ao fechar caixa: " + e.getMessage());
                        return erro;
                }
        }

        // 5. CONSULTAS

        /**
         * Lista todas as vendas do dia
         * 
         * @return Map com lista de vendas e totalizadores
         */
        public Map<String, Object> listarVendasDoDia() {
                LocalDate hoje = LocalDate.now();

                try {
                        List<Caixa> vendas = caixaRepository.findByDataAndTipo(hoje, "ENTRADA");

                        Double total = vendas.stream()
                                        .mapToDouble(c -> c.getSaldo() != null ? c.getSaldo() : 0.0)
                                        .sum();

                        // Converter vendas para Map simples para evitar problemas de serialização
                        List<Map<String, Object>> vendasFormatadas = new java.util.ArrayList<>();
                        for (Caixa v : vendas) {
                                Map<String, Object> venda = new java.util.HashMap<>();
                                venda.put("id", v.getId());
                                venda.put("descricao", v.getDescricao() != null ? v.getDescricao() : "");
                                venda.put("observacoes", v.getObservacoes() != null ? v.getObservacoes() : "");
                                venda.put("saldo", v.getSaldo() != null ? v.getSaldo() : 0.0);
                                venda.put("data", v.getData().toString());
                                vendasFormatadas.add(venda);
                        }

                        Map<String, Object> resultado = new java.util.HashMap<>();
                        resultado.put("sucesso", true);
                        resultado.put("data", hoje.toString());
                        resultado.put("totalVendas", vendas.size());
                        resultado.put("totalArrecadado", total);
                        resultado.put("vendas", vendasFormatadas);

                        return resultado;
                } catch (Exception e) {
                        Map<String, Object> resultado = new java.util.HashMap<>();
                        resultado.put("sucesso", true);
                        resultado.put("data", hoje.toString());
                        resultado.put("totalVendas", 0);
                        resultado.put("totalArrecadado", 0.0);
                        resultado.put("vendas", new java.util.ArrayList<>());
                        return resultado;
                }
        }

        /**
         * Obtém o status atual do caixa
         * 
         * @return Map com status (aberto/fechado) e detalhes
         */
        public Map<String, Object> obterStatus() {
                LocalDate hoje = LocalDate.now();

                try {
                        Optional<Caixa> caixaAberto = caixaRepository.findByDataAndTipoAndStatus(hoje, "ABERTURA", "ABERTO");

                        if (caixaAberto.isPresent()) {
                                Caixa caixa = caixaAberto.get();
                                List<Caixa> vendas = caixaRepository.findByDataAndTipo(hoje, "ENTRADA");
                                Double totalVendas = vendas.stream()
                                                .mapToDouble(c -> c.getSaldo() != null ? c.getSaldo() : 0.0)
                                                .sum();

                                Map<String, Object> resultado = new java.util.HashMap<>();
                                resultado.put("aberto", true);
                                resultado.put("caixaId", caixa.getId());
                                resultado.put("horarioAbertura", caixa.getHorarioAbertura() != null ? caixa.getHorarioAbertura().toString() : "");
                                resultado.put("totalVendas", vendas.size());
                                resultado.put("totalArrecadado", totalVendas);
                                resultado.put("data", hoje.toString());
                                return resultado;
                        }

                        Map<String, Object> resultado = new java.util.HashMap<>();
                        resultado.put("aberto", false);
                        resultado.put("mensagem", "Caixa fechado para hoje. Abra o caixa para começar.");
                        resultado.put("data", hoje.toString());
                        return resultado;
                } catch (Exception e) {
                        System.err.println("Erro em obterStatus: " + e.getMessage());
                        e.printStackTrace();
                        
                        Map<String, Object> resultado = new java.util.HashMap<>();
                        resultado.put("aberto", false);
                        resultado.put("mensagem", "Erro ao verificar status do caixa");
                        resultado.put("data", LocalDate.now().toString());
                        return resultado;
                }
        }
}
