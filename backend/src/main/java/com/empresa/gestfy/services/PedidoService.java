package com.empresa.gestfy.services;

import com.empresa.gestfy.config.DataHoraBrasil;
import com.empresa.gestfy.dto.pedido.PedidoDTO;
import com.empresa.gestfy.dto.pedido.PedidoItemDTO;
import com.empresa.gestfy.dto.pedido.PedidoItemRequest;
import com.empresa.gestfy.dto.pedido.PedidoRequest;
import com.empresa.gestfy.models.Caixa;
import com.empresa.gestfy.models.Cliente;
import com.empresa.gestfy.models.Estoque;
import com.empresa.gestfy.models.Pedido;
import com.empresa.gestfy.models.PedidoItem;
import com.empresa.gestfy.models.Produto;
import com.empresa.gestfy.repositories.CaixaRepository;
import com.empresa.gestfy.repositories.ClienteRepository;
import com.empresa.gestfy.repositories.EstoqueRepository;
import com.empresa.gestfy.repositories.PedidoRepository;
import com.empresa.gestfy.repositories.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * PedidoService
 * Responsável pela lógica de pedidos incluindo:
 * - Criação de pedidos
 * - Atualização de status
 * - Registro no caixa
 */
@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;
    private final ClienteRepository clienteRepository;
    private final EstoqueRepository estoqueRepository;
    private final CaixaRepository caixaRepository;

    public PedidoService(
            PedidoRepository pedidoRepository,
            ProdutoRepository produtoRepository,
            ClienteRepository clienteRepository,
            EstoqueRepository estoqueRepository,
            CaixaRepository caixaRepository) {
        this.pedidoRepository = pedidoRepository;
        this.produtoRepository = produtoRepository;
        this.clienteRepository = clienteRepository;
        this.estoqueRepository = estoqueRepository;
        this.caixaRepository = caixaRepository;
    }

    /**
     * Criar novo pedido
     */
    @Transactional
    public PedidoDTO criar(PedidoRequest request) {
        try {
            System.out.println("\n=== INICIANDO CRIAÇÃO DE PEDIDO ===");
            System.out.println("Cliente: " + request.nomeCliente());
            System.out.println("Telefone: " + request.telefoneCliente());
            System.out.println("Itens: " + (request.itens() != null ? request.itens().size() : 0));

            // 1. Buscar ou criar cliente
            System.out.println("\n[PASSO 1] Buscando ou criando cliente...");
            Cliente cliente = null;
            try {
                cliente = clienteRepository.findAll().stream()
                        .filter(c -> c.getTelefone() != null && c.getTelefone().equals(request.telefoneCliente()))
                        .findFirst()
                        .orElseGet(() -> {
                            System.out.println("  → Cliente não encontrado, criando novo...");
                            Cliente novoCliente = new Cliente();
                            novoCliente.setNome(request.nomeCliente());
                            novoCliente.setTelefone(request.telefoneCliente());
                            // Gerar email único baseado no telefone para evitar constraint violations
                            String emailUnico = request.telefoneCliente().replaceAll("[^0-9]", "") + "@cliente.gestfy";
                            novoCliente.setEmail(emailUnico);
                            novoCliente.setEndereco(request.endereco() != null ? request.endereco() : "");
                            Cliente salvo = clienteRepository.save(novoCliente);
                            System.out.println("  → Cliente criado com ID: " + salvo.getId());
                            return salvo;
                        });
                System.out.println("✓ Cliente obtido: ID=" + cliente.getId() + ", Nome=" + cliente.getNome());
            } catch (Exception e) {
                System.out.println(
                        "✗ ERRO ao buscar/criar cliente: " + e.getClass().getSimpleName() + " - " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("Erro ao processar cliente: " + e.getMessage(), e);
            }

            if (cliente == null) {
                throw new RuntimeException("Cliente não pode ser nulo");
            }

            // 2. Criar pedido
            System.out.println("\n[PASSO 2] Criando entidade Pedido...");
            Pedido pedido = new Pedido();
            pedido.setCliente(cliente);
            // Forçar pagamento somente em dinheiro conforme regra do sistema
            pedido.setFormaPagamento("DINHEIRO");
            pedido.setFormaRecebimento(request.formaRecebimento());
            pedido.setEndereco(request.endereco());
            pedido.setStatus("RECEBIDO");
            // Registrar informações de troco
            pedido.setPrecisaTroco(request.precisaTroco() != null ? request.precisaTroco() : false);
            pedido.setValorTroco(request.valorTroco());
            LocalDateTime agora = DataHoraBrasil.agora();
            System.out.println("  → Data/Hora: " + agora);
            pedido.setData(agora);
            System.out.println("✓ Pedido criado em memória");
            System.out.println("  Precisa troco? " + pedido.getPrecisaTroco());
            if (pedido.getPrecisaTroco()) {
                System.out.println("  Valor do troco: R$ " + pedido.getValorTroco());
            }

            // 3. Validar e processar itens
            System.out.println("\n[PASSO 3] Processando " + request.itens().size() + " itens do pedido...");
            List<PedidoItem> itens = new ArrayList<>();
            Double totalPedido = 0.0;

            for (int i = 0; i < request.itens().size(); i++) {
                PedidoItemRequest itemReq = request.itens().get(i);
                System.out.println("\n  [Item " + (i + 1) + "]");
                System.out.println("    ID Produto: " + itemReq.getIdProduto());
                System.out.println("    Quantidade solicitada: " + itemReq.getQuantidade());

                try {
                    Produto produto = produtoRepository.findById(itemReq.getIdProduto())
                            .orElseThrow(
                                    () -> new RuntimeException("Produto não encontrado: ID " + itemReq.getIdProduto()));

                    System.out.println("    ✓ Produto encontrado: " + produto.getNome());
                    System.out.println("    Quantidade em estoque: "
                            + (produto.getQuantidade() != null ? produto.getQuantidade() : "NULL"));
                    System.out.println("    Preço: " + (produto.getPreco() != null ? produto.getPreco() : "NULL"));
                    System.out.println("    Categoria ID: "
                            + (produto.getCategoria() != null ? produto.getCategoria().getId() : "NULL"));

                    if (produto.getQuantidade() == null || produto.getQuantidade() < itemReq.getQuantidade()) {
                        System.out.println("    ✗ ERRO: Quantidade insuficiente");
                        throw new RuntimeException("Quantidade insuficiente do produto: " + produto.getNome());
                    }

                    PedidoItem item = new PedidoItem();
                    item.setPedido(pedido);
                    item.setProduto(produto);
                    item.setQuantidade(itemReq.getQuantidade() != null ? itemReq.getQuantidade() : 1);
                    item.setPrecoUnitario(produto.getPreco() != null ? produto.getPreco() : 0.0);

                    // Descontar quantidade - SEGURO CONTRA NULL
                    Integer quantidadeAtual = produto.getQuantidade() != null ? produto.getQuantidade() : 0;
                    Integer novaQuantidade = quantidadeAtual - item.getQuantidade();
                    produto.setQuantidade(novaQuantidade);

                    try {
                        System.out.println("    → Atualizando quantidade do produto...");
                        produtoRepository.save(produto);
                        System.out.println("    ✓ Produto salvo com nova quantidade: " + novaQuantidade);
                    } catch (Exception e) {
                        System.out.println("    ✗ ERRO ao salvar produto: " + e.getClass().getSimpleName() + " - "
                                + e.getMessage());
                        e.printStackTrace();
                        throw new RuntimeException("Erro ao atualizar estoque do produto: " + e.getMessage(), e);
                    }

                    // Registrar saída no estoque
                    try {
                        System.out.println("    → Registrando movimento de estoque...");
                        Estoque movimento = new Estoque();
                        movimento.setProduto(produto);
                        movimento.setTipoMovimento("SAIDA");
                        movimento.setQuantidade(item.getQuantidade());
                        movimento.setDataMovimento(DataHoraBrasil.agora());
                        estoqueRepository.save(movimento);
                        System.out.println("    ✓ Movimento de estoque registrado");
                    } catch (Exception e) {
                        System.out.println("    ✗ ERRO ao registrar movimento de estoque: "
                                + e.getClass().getSimpleName() + " - " + e.getMessage());
                        e.printStackTrace();
                        throw new RuntimeException("Erro ao registrar movimento de estoque: " + e.getMessage(), e);
                    }

                    // Adicionar item ao pedido
                    pedido.addItem(item);
                    totalPedido += item.getPrecoUnitario() * item.getQuantidade();
                    System.out.println("    ✓ Item adicionado ao pedido");

                } catch (RuntimeException e) {
                    System.out.println("    ✗ ERRO ao processar item: " + e.getMessage());
                    throw e;
                }
            }

            System.out.println("\n[PASSO 4] Finalizando pedido...");
            System.out.println("  Total do pedido: " + totalPedido);
            pedido.setTotal(totalPedido);

            try {
                System.out.println("  → Salvando pedido no banco...");
                Pedido salvo = pedidoRepository.save(pedido);
                System.out.println("✓ Pedido salvo com ID: " + salvo.getId());

                System.out.println("\n[PASSO 5] Recarregando pedido com relacionamentos...");
                Pedido pedidoRecarregado = pedidoRepository.findByIdWithRelationships(salvo.getId())
                        .orElseThrow(() -> new RuntimeException("Erro ao recarregar pedido após salvar"));
                System.out.println("✓ Pedido recarregado com sucesso");

                System.out.println("\n[PASSO 6] Convertendo para DTO...");
                PedidoDTO dto = toDTO(pedidoRecarregado);
                System.out.println("✓ Conversão para DTO realizada com sucesso");
                System.out.println("=== CRIAÇÃO DE PEDIDO FINALIZADA COM SUCESSO ===");
                return dto;

            } catch (Exception e) {
                System.out.println("✗ ERRO ao salvar pedido ou converter para DTO: " + e.getClass().getSimpleName()
                        + " - " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("Erro ao salvar pedido: " + e.getMessage(), e);
            }

        } catch (Exception e) {
            System.out.println("\n✗ ERRO GERAL NA CRIAÇÃO DE PEDIDO:");
            System.out.println("  Tipo: " + e.getClass().getSimpleName());
            System.out.println("  Mensagem: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erro ao criar pedido: " + e.getMessage(), e);
        }
    }

    /**
     * Listar todos os pedidos com relacionamentos carregados
     * 
     * @Transactional garante que os dados LAZY sejam carregados dentro da transação
     */
    @Transactional(readOnly = true)
    public List<PedidoDTO> listar() {
        return pedidoRepository.findAllWithRelationships()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Buscar pedido por ID com relacionamentos carregados
     */
    @Transactional(readOnly = true)
    public Optional<PedidoDTO> buscarPorId(Long id) {
        return pedidoRepository.findByIdWithRelationships(id)
                .map(this::toDTO);
    }

    /**
     * Atualizar status do pedido com relacionamentos carregados
     */
    @Transactional
    public PedidoDTO atualizarStatus(Long id, String novoStatus) {
        Pedido pedido = pedidoRepository.findByIdWithRelationships(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        String statusAnterior = pedido.getStatus();
        System.out.println("\n=== ATUALIZAÇÃO DE STATUS DO PEDIDO #" + id + " ===");
        System.out.println("Status anterior: " + statusAnterior);
        System.out.println("Novo status: " + novoStatus);

        pedido.setStatus(novoStatus);

        // Registrar no caixa SOMENTE quando transiciona para FINALIZADO (apenas uma
        // vez)
        if ("FINALIZADO".equals(novoStatus) && !"FINALIZADO".equals(statusAnterior)) {
            System.out.println("[CAIXA] Registrando pedido no caixa...");
            registrarNoCaixa(pedido);
        } else if ("FINALIZADO".equals(novoStatus) && "FINALIZADO".equals(statusAnterior)) {
            System.out.println("[CAIXA] Pedido já estava finalizado - ignorando duplicação");
        }

        Pedido atualizado = pedidoRepository.save(pedido);
        System.out.println("✓ Status atualizado com sucesso");
        System.out.println("=== FIM DA ATUALIZAÇÃO ===\n");
        return toDTO(atualizado);
    }

    /**
     * Remover pedido
     */
    public void remover(Long id) {
        if (!pedidoRepository.existsById(id)) {
            throw new RuntimeException("Pedido não encontrado");
        }
        pedidoRepository.deleteById(id);
    }

    /**
     * Listar pedidos por status
     * Útil para o admin controlar o workflow dos pedidos
     */
    @Transactional(readOnly = true)
    public List<PedidoDTO> listarPorStatus(String status) {
        System.out.println("\n[listarPorStatus] Buscando pedidos com status: " + status);
        try {
            List<Pedido> pedidos = pedidoRepository.findByStatusWithRelationships(status);
            System.out.println("✓ " + pedidos.size() + " pedidos encontrados com status: " + status);
            return pedidos.stream()
                    .map(this::toDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.out.println("✗ ERRO ao listar pedidos por status: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erro ao listar pedidos por status: " + e.getMessage(), e);
        }
    }

    /**
     * Registrar pedido no caixa como entrada financeira
     * Regras:
     * - Somente quando pedido é FINALIZADO
     * - Registra apenas uma vez (verifica duplicidade)
     * - Origem: PEDIDO_ONLINE
     */
    private void registrarNoCaixa(Pedido pedido) {
        System.out.println("\n  [registrarNoCaixa] Iniciando registro do pedido #" + pedido.getId() + " no caixa");

        // VERIFICAÇÃO DE DUPLICIDADE
        if (pedido.getCaixaRegistroId() != null) {
            System.out.println("  ✓ Pedido já foi registrado no caixa (ID: " + pedido.getCaixaRegistroId() + ")");
            System.out.println("  → Ignorando duplicação");
            return;
        }

        // CRIAR NOVO REGISTRO NO CAIXA
        Caixa registro = new Caixa();
        registro.setTipo("ENTRADA");
        registro.setData(LocalDate.now());
        registro.setDataAbertura(DataHoraBrasil.agora());
        registro.setHorarioAbertura(DataHoraBrasil.agora()); // Compatibilidade
        registro.setStatus("CONCLUIDO");
        registro.setValorInicial(0.0);
        registro.setSaldo(pedido.getTotal());

        // Descrição com informações do pedido
        registro.setDescricao(
                String.format("Pedido Online: #%d - Cliente: %s", pedido.getId(), pedido.getCliente().getNome()));

        // Observações com detalhes do pedido
        StringBuilder obs = new StringBuilder();
        obs.append("Forma de pagamento: ").append(pedido.getFormaPagamento());
        obs.append(" | Forma de recebimento: ").append(pedido.getFormaRecebimento());
        if (pedido.getPrecisaTroco() != null && pedido.getPrecisaTroco()) {
            obs.append(" | Troco: R$ ").append(String.format("%.2f", pedido.getValorTroco()));
        }
        registro.setObservacoes(obs.toString());

        // ORIGEM DO REGISTRO (importante para rastreamento)
        registro.setOrigem("PEDIDO_ONLINE");

        System.out.println("  → Salvando registro no caixa...");
        System.out.println("    Valor: R$ " + String.format("%.2f", pedido.getTotal()));
        System.out.println("    Origem: PEDIDO_ONLINE");

        Caixa registroSalvo = caixaRepository.save(registro);

        // RASTREAMENTO: Guardar ID do registro de caixa no pedido para evitar
        // duplicidade
        pedido.setCaixaRegistroId(registroSalvo.getId());
        pedidoRepository.save(pedido);

        System.out.println("  ✓ Registro criado no caixa com ID: " + registroSalvo.getId());
        System.out.println("  ✓ Rastreamento guardado no pedido");
    }

    /**
     * Converter modelo para DTO
     * Garante que todos os dados necessários estão disponíveis
     */
    @Transactional(readOnly = true)
    private PedidoDTO toDTO(Pedido pedido) {
        try {
            System.out.println("\n  [toDTO] Iniciando conversão para DTO...");

            // Validações de segurança
            if (pedido == null) {
                System.out.println("  ✗ [toDTO] ERRO: Pedido é nulo");
                throw new RuntimeException("Pedido não pode ser nulo");
            }

            System.out.println("  [toDTO] Pedido ID: " + pedido.getId());

            if (pedido.getCliente() == null) {
                System.out.println("  ✗ [toDTO] ERRO: Cliente é nulo no pedido " + pedido.getId());
                throw new RuntimeException("Cliente não pode ser nulo no pedido: " + pedido.getId());
            }

            System.out.println("  [toDTO] Cliente: " + pedido.getCliente().getNome());

            // Converter itens do pedido
            List<PedidoItem> itens = pedido.getItens() != null ? pedido.getItens() : new ArrayList<>();
            System.out.println("  [toDTO] Itens do pedido: " + itens.size());

            List<PedidoItemDTO> itensDTO = itens
                    .stream()
                    .map(item -> {
                        try {
                            if (item == null) {
                                System.out.println("  ✗ [toDTO] ERRO: Item é nulo");
                                throw new RuntimeException("Item não pode ser nulo");
                            }

                            if (item.getProduto() == null) {
                                System.out.println("  ✗ [toDTO] ERRO: Produto é nulo no item " + item.getId());
                                throw new RuntimeException("Produto não pode ser nulo no item: " + item.getId());
                            }

                            Produto prod = item.getProduto();
                            System.out.println("    [toDTO Item] ID: " + item.getId() + ", Produto: " + prod.getNome() +
                                    ", Categoria ID: "
                                    + (prod.getCategoria() != null ? prod.getCategoria().getId() : "NULL"));

                            return new PedidoItemDTO(
                                    item.getId(),
                                    item.getProduto().getId(),
                                    item.getProduto().getNome(),
                                    item.getPrecoUnitario(),
                                    item.getQuantidade(),
                                    item.getPrecoUnitario() * item.getQuantidade() // subtotal
                            );
                        } catch (Exception e) {
                            System.out.println("  ✗ [toDTO] ERRO ao converter item: " + e.getClass().getSimpleName()
                                    + " - " + e.getMessage());
                            e.printStackTrace();
                            throw new RuntimeException("Erro ao converter item para DTO: " + e.getMessage(), e);
                        }
                    })
                    .collect(Collectors.toList());

            // Construir DTO com dados seguros
            System.out.println("  [toDTO] Total do pedido: " + pedido.getTotal());
            System.out.println("  ✓ [toDTO] Conversão concluída com sucesso");

            return new PedidoDTO(
                    pedido.getId(),
                    pedido.getCliente().getNome(),
                    pedido.getCliente().getTelefone(),
                    pedido.getEndereco(),
                    pedido.getFormaPagamento(),
                    pedido.getFormaRecebimento(),
                    pedido.getStatus(),
                    pedido.getTotal(), // Agora seguro, pois itens estão carregados
                    pedido.getData(),
                    pedido.getPrecisaTroco(),
                    pedido.getValorTroco(),
                    pedido.getCaixaRegistroId(),
                    itensDTO);
        } catch (Exception e) {
            System.out.println("\n  ✗ [toDTO] ERRO GERAL NA CONVERSÃO:");
            System.out.println("    Tipo: " + e.getClass().getSimpleName());
            System.out.println("    Mensagem: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erro ao converter pedido para DTO: " + e.getMessage(), e);
        }
    }
}
