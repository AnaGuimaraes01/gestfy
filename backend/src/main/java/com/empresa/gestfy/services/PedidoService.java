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
        pedido.setFormaPagamento(request.formaPagamento());
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
            movimento.setProdutoId(produto.getId());
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
     * Listar todos os pedidos
     */
    public List<PedidoDTO> listar() {
        return pedidoRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Buscar pedido por ID
     */
    public Optional<PedidoDTO> buscarPorId(Long id) {
        return pedidoRepository.findById(id)
                .map(this::toDTO);
    }

    /**
     * Atualizar status do pedido
     */
    public PedidoDTO atualizarStatus(Long id, String novoStatus) {
        Pedido pedido = pedidoRepository.findById(id)
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
        registro.setHorarioAbertura(LocalDateTime.now());
        registro.setStatus("ABERTO");
        registro.setSaldo(pedido.getTotal());
        registro.setDescricao(
                String.format("Pedido: #%d - Cliente: %s", pedido.getId(), pedido.getCliente().getNome()));
        registro.setObservacoes(String.format("Forma de pagamento: %s | Forma de recebimento: %s",
                pedido.getFormaPagamento(), pedido.getFormaRecebimento()));

        caixaRepository.save(registro);
    }

    /**
     * Converter modelo para DTO
     */
    private PedidoDTO toDTO(Pedido pedido) {
        List<PedidoItemDTO> itensDTO = pedido.getItens().stream()
                .map(item -> new PedidoItemDTO(
                        item.getId(),
                        item.getProduto().getId(),
                        item.getProduto().getNome(),
                        item.getPrecoUnitario(),
                        item.getQuantidade(),
                        item.getPrecoUnitario() * item.getQuantidade() // subtotal
                ))
                .collect(Collectors.toList());

        return new PedidoDTO(
                pedido.getId(),
                pedido.getCliente().getNome(),
                pedido.getCliente().getTelefone(),
                pedido.getEndereco(),
                pedido.getFormaPagamento(),
                pedido.getFormaRecebimento(),
                pedido.getStatus(),
                pedido.getTotal(),
                pedido.getData(),
                itensDTO);
    }
}
