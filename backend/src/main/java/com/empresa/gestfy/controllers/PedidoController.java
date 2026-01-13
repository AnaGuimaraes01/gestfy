package com.empresa.gestfy.controllers;

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

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;
    private final ClienteRepository clienteRepository;
    private final EstoqueRepository estoqueRepository;
    private final CaixaRepository caixaRepository;

    public PedidoController(
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

   
    // CRIAR PEDIDO
    @PostMapping
    public ResponseEntity<PedidoDTO> criarPedido(@RequestBody @Valid PedidoRequest request) {

        // 1️⃣ Busca o cliente
        Cliente cliente = clienteRepository.findById(request.clienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado: ID " + request.clienteId()));

        // 2️⃣ Cria o pedido
        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setFormaPagamento(request.formaPagamento());
        pedido.setFormaRecebimento(request.formaRecebimento());
        pedido.setEndereco(request.endereco());
        pedido.setStatus("RECEBIDO");
        pedido.setData(LocalDateTime.now());

        // 3️⃣ Cria os itens do pedido
        List<PedidoItem> itens = new ArrayList<>();
        for (PedidoItemRequest itemReq : request.itens()) {
            Produto produto = produtoRepository.findById(itemReq.getIdProduto())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado: ID " + itemReq.getIdProduto()));

            PedidoItem item = new PedidoItem();
            item.setPedido(pedido); // importante!
            item.setProduto(produto); // importante!
            item.setQuantidade(itemReq.getQuantidade() != null ? itemReq.getQuantidade() : 1);
            item.setPrecoUnitario(produto.getPreco() != null ? produto.getPreco() : 0.0);

            itens.add(item);
        }

        // 4️⃣ Adiciona os itens ao pedido usando addItem()
        for (PedidoItem item : itens) {
            pedido.addItem(item);
        }

        // 5️⃣ Calcula e define o total
        Double totalCalculado = pedido.getItens().stream()
                .mapToDouble(i -> i.getPrecoUnitario() * i.getQuantidade())
                .sum();
        pedido.setTotal(totalCalculado > 0 ? totalCalculado : 0.0);

        // 6️⃣ Salva o pedido (JPA salva os itens automaticamente)
        pedido = pedidoRepository.save(pedido);

        // 7️⃣ NOVO: Registra movimento de SAIDA no estoque para cada item
        for (PedidoItem item : pedido.getItens()) {
            Estoque movimento = new Estoque();
            movimento.setProdutoId(item.getProduto().getId());
            movimento.setTipoMovimento("SAIDA");
            movimento.setQuantidade(item.getQuantidade());
            movimento.setDataMovimento(LocalDateTime.now());
            estoqueRepository.save(movimento);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(mapToDTO(pedido));
    }

    // =========================
    // LISTAR TODOS
    // =========================
    @GetMapping
    public ResponseEntity<List<PedidoDTO>> listarPedidos() {
        List<PedidoDTO> pedidos = pedidoRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(pedidos);
    }

    // =========================
    // BUSCAR POR ID
    // =========================
    @GetMapping("/{id}")
    public ResponseEntity<PedidoDTO> buscarPorId(@PathVariable Long id) {
        return pedidoRepository.findById(id)
                .map(p -> ResponseEntity.ok(mapToDTO(p)))
                .orElse(ResponseEntity.notFound().build());
    }

    // =========================
    // ATUALIZAR STATUS
    // =========================
    @PutMapping("/{id}/status")
    public ResponseEntity<PedidoDTO> atualizarStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado: ID " + id));

        // Validar transição de status
        validarTransicaoStatus(pedido.getStatus(), status);

        pedido.setStatus(status);
        pedidoRepository.save(pedido);

        // 🔥 NOVO: Se o pedido foi FINALIZADO, registra automaticamente no CAIXA
        if (status.equals("FINALIZADO")) {
            registrarVendaNoCaixa(pedido);
        }

        return ResponseEntity.ok(mapToDTO(pedido));
    }

    // =========================
    // REGISTRAR VENDA NO CAIXA (automático)
    // =========================
    private void registrarVendaNoCaixa(Pedido pedido) {
        try {
            Caixa caixa = new Caixa();
            caixa.setSaldo(pedido.getTotal() != null ? pedido.getTotal() : 0.0);
            caixa.setDescricao("Venda #" + pedido.getId() + " - Cliente: " + pedido.getCliente().getNome());
            caixa.setData(LocalDate.now());

            caixaRepository.save(caixa);
            System.out.println("✅ Venda registrada no caixa: Pedido #" + pedido.getId());
        } catch (Exception e) {
            System.err.println("❌ Erro ao registrar venda no caixa: " + e.getMessage());
            // Não interrompe o fluxo se falhar no registro do caixa
        }
    }

    // =========================
    // VALIDAR TRANSIÇÃO DE STATUS
    // =========================
    private void validarTransicaoStatus(String statusAtual, String novoStatus) {
        List<String> statusValidos = Arrays.asList("RECEBIDO", "EM_PREPARO", "PRONTO_RETIRADA", "SAIU_ENTREGA", "FINALIZADO", "CANCELADO");
        if (!statusValidos.contains(novoStatus)) {
            throw new RuntimeException("Status inválido: " + novoStatus);
        }
        if (statusAtual.equals("FINALIZADO")) {
            throw new RuntimeException("Pedido já foi finalizado, não pode ser alterado");
        }
        // Permite finalizar a partir de qualquer status, exceto FINALIZADO
        if (novoStatus.equals("FINALIZADO")) {
            return;
        }
        // Permite CANCELADO a partir de qualquer status, exceto FINALIZADO
        if (novoStatus.equals("CANCELADO")) {
            return;
        }
        // Mantém as transições originais para os demais
        if (statusAtual.equals("RECEBIDO")) {
            if (!novoStatus.equals("EM_PREPARO")) {
                throw new RuntimeException("De RECEBIDO só é possível ir para EM_PREPARO ou FINALIZADO/CANCELADO");
            }
        } else if (statusAtual.equals("EM_PREPARO")) {
            if (!novoStatus.equals("PRONTO_RETIRADA") && !novoStatus.equals("SAIU_ENTREGA")) {
                throw new RuntimeException("De EM_PREPARO só é possível ir para PRONTO_RETIRADA, SAIU_ENTREGA ou FINALIZADO/CANCELADO");
            }
        } else if (statusAtual.equals("PRONTO_RETIRADA")) {
            if (!novoStatus.equals("FINALIZADO")) {
                throw new RuntimeException("De PRONTO_RETIRADA só é possível ir para FINALIZADO ou CANCELADO");
            }
        } else if (statusAtual.equals("SAIU_ENTREGA")) {
            if (!novoStatus.equals("FINALIZADO")) {
                throw new RuntimeException("De SAIU_ENTREGA só é possível ir para FINALIZADO ou CANCELADO");
            }
        }
    }

    // =========================
    // REMOVER PEDIDO
    // =========================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removerPedido(@PathVariable Long id) {

        if (!pedidoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        pedidoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // =========================
    // HELPER: MAPEAR PARA DTO
    // =========================
    private PedidoDTO mapToDTO(Pedido pedido) {
        // Mapeamento seguro dos itens
        List<PedidoItemDTO> itensDTO = new ArrayList<>();
        if (pedido.getItens() != null) {
            itensDTO = pedido.getItens().stream()
                    .filter(item -> item != null && item.getProduto() != null)
                    .map(item -> new PedidoItemDTO(
                            item.getId(),
                            item.getProduto().getId(),
                            item.getProduto().getNome() != null ? item.getProduto().getNome() : "Produto Indefinido",
                            item.getPrecoUnitario() != null ? item.getPrecoUnitario() : 0.0,
                            item.getQuantidade() != null ? item.getQuantidade() : 0,
                            (item.getPrecoUnitario() != null ? item.getPrecoUnitario() : 0.0) *
                                    (item.getQuantidade() != null ? item.getQuantidade() : 0)))
                    .collect(Collectors.toList());
        }

        // Garante que total nunca seja null
        Double totalFinal = pedido.getTotal();
        if (totalFinal == null || totalFinal <= 0) {
            totalFinal = itensDTO.stream()
                    .mapToDouble(PedidoItemDTO::getSubtotal)
                    .sum();
        }

        // Obter dados do cliente de forma segura
        String nomeCliente = "Cliente Indefinido";
        String telefonCliente = "N/A";
        
        if (pedido.getCliente() != null) {
            if (pedido.getCliente().getNome() != null) {
                nomeCliente = pedido.getCliente().getNome();
            }
            if (pedido.getCliente().getTelefone() != null) {
                telefonCliente = pedido.getCliente().getTelefone();
            }
        }

        return new PedidoDTO(
                pedido.getId(),
                nomeCliente,
                telefonCliente,
                pedido.getEndereco() != null ? pedido.getEndereco() : "Não informado",
                pedido.getFormaPagamento() != null ? pedido.getFormaPagamento() : "N/A",
                pedido.getFormaRecebimento() != null ? pedido.getFormaRecebimento() : "N/A",
                pedido.getStatus() != null ? pedido.getStatus() : "RECEBIDO",
                totalFinal > 0 ? totalFinal : 0.0,
                pedido.getData(),
                itensDTO);
    }
}
