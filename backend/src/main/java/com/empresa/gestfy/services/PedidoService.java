package com.empresa.gestfy.services;

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
        // 1. Buscar ou criar cliente
        Cliente cliente = clienteRepository.findAll().stream()
                .filter(c -> c.getTelefone() != null && c.getTelefone().equals(request.telefoneCliente()))
                .findFirst()
                .orElseGet(() -> {
                    Cliente novoCliente = new Cliente();
                    novoCliente.setNome(request.nomeCliente());
                    novoCliente.setTelefone(request.telefoneCliente());
                    novoCliente.setEmail("");
                    novoCliente.setEndereco("");
                    return clienteRepository.save(novoCliente);
                });

        // 2. Criar pedido
        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        // Forçar pagamento somente em dinheiro conforme regra do sistema
        pedido.setFormaPagamento("DINHEIRO");
        pedido.setFormaRecebimento(request.formaRecebimento());
        pedido.setEndereco(request.endereco());
        pedido.setStatus("RECEBIDO");
        pedido.setData(LocalDateTime.now());

        // 3. Validar e processar itens
        List<PedidoItem> itens = new ArrayList<>();
        Double totalPedido = 0.0;

        for (PedidoItemRequest itemReq : request.itens()) {
            Produto produto = produtoRepository.findById(itemReq.getIdProduto())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado: ID " + itemReq.getIdProduto()));

            if (produto.getQuantidade() == null || produto.getQuantidade() < itemReq.getQuantidade()) {
                throw new RuntimeException("Quantidade insuficiente do produto: " + produto.getNome());
            }

            PedidoItem item = new PedidoItem();
            item.setPedido(pedido);
            item.setProduto(produto);
            item.setQuantidade(itemReq.getQuantidade() != null ? itemReq.getQuantidade() : 1);
            item.setPrecoUnitario(produto.getPreco() != null ? produto.getPreco() : 0.0);

            // Descontar quantidade
            Integer novaQuantidade = produto.getQuantidade() - item.getQuantidade();
            produto.setQuantidade(novaQuantidade);
            produtoRepository.save(produto);

            // Registrar saída no estoque
            Estoque movimento = new Estoque();
            movimento.setProduto(produto);
            movimento.setTipoMovimento("SAIDA");
            movimento.setQuantidade(item.getQuantidade());
            movimento.setDataMovimento(LocalDateTime.now());
            estoqueRepository.save(movimento);

            // Adicionar item ao pedido
            pedido.addItem(item);
            totalPedido += item.getPrecoUnitario() * item.getQuantidade();
        }

        pedido.setTotal(totalPedido);

        Pedido salvo = pedidoRepository.save(pedido);
        return toDTO(salvo);
    }

    /**
     * Listar todos os pedidos com relacionamentos carregados
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
        pedido.setStatus(novoStatus);

        // Se o pedido é finalizado e era "EM PREPARACAO", registra no caixa
        if ("FINALIZADO".equals(novoStatus) && "EM_PREPARACAO".equals(statusAnterior)) {
            registrarNoCaixa(pedido);
        }

        Pedido atualizado = pedidoRepository.save(pedido);
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
     * Registrar pedido no caixa como entrada
     */
    private void registrarNoCaixa(Pedido pedido) {
        Caixa registro = new Caixa();
        registro.setTipo("ENTRADA");
        registro.setData(LocalDate.now());
        registro.setDataAbertura(LocalDateTime.now());
        registro.setHorarioAbertura(LocalDateTime.now()); // Mantém por compatibilidade
        registro.setStatus("ABERTO");
        registro.setValorInicial(0.0); // Garantir que nunca seja null
        registro.setSaldo(pedido.getTotal());
        registro.setDescricao(
                String.format("Pedido: #%d - Cliente: %s", pedido.getId(), pedido.getCliente().getNome()));
        registro.setObservacoes(String.format("Forma de pagamento: %s | Forma de recebimento: %s",
                pedido.getFormaPagamento(), pedido.getFormaRecebimento()));

        caixaRepository.save(registro);
    }

    /**
     * Converter modelo para DTO
     * Garante que todos os dados necessários estão disponíveis
     */
    private PedidoDTO toDTO(Pedido pedido) {
        // Validações de segurança
        if (pedido == null) {
            throw new RuntimeException("Pedido não pode ser nulo");
        }
        if (pedido.getCliente() == null) {
            throw new RuntimeException("Cliente não pode ser nulo no pedido: " + pedido.getId());
        }

        // Converter itens do pedido
        List<PedidoItemDTO> itensDTO = (pedido.getItens() != null ? pedido.getItens() : new ArrayList<>())
                .stream()
                .map(item -> {
                    if (item.getProduto() == null) {
                        throw new RuntimeException("Produto não pode ser nulo no item: " + item.getId());
                    }
                    return new PedidoItemDTO(
                            item.getId(),
                            item.getProduto().getId(),
                            item.getProduto().getNome(),
                            item.getPrecoUnitario(),
                            item.getQuantidade(),
                            item.getPrecoUnitario() * item.getQuantidade() // subtotal
                    );
                })
                .collect(Collectors.toList());

        // Construir DTO com dados seguros
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
                itensDTO
        );
    }
}
