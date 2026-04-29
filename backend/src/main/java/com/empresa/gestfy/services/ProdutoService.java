package com.empresa.gestfy.services;

import com.empresa.gestfy.dto.caixa.ProdutoBuscaResponse;
import com.empresa.gestfy.dto.produto.ProdutoDTO;
import com.empresa.gestfy.dto.produto.ProdutoRequest;
import com.empresa.gestfy.models.Produto;
import com.empresa.gestfy.repositories.ProdutoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * ProdutoService
 * Responsável por operações de produtos (busca, validação, CRUD, etc)
 * Separa a lógica de negócio da camada HTTP.
 */
@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final EstoqueService estoqueService;

    public ProdutoService(ProdutoRepository produtoRepository, EstoqueService estoqueService) {
        this.produtoRepository = produtoRepository;
        this.estoqueService = estoqueService;
    }

    // ========================================
    // OPERAÇÕES CRUD
    // ========================================

    /**
     * Criar novo produto
     */
    public ProdutoDTO criar(ProdutoRequest request) {
        Produto produto = new Produto(
                request.nome(),
                request.descricao(),
                request.preco(),
                request.urlFoto(),
                request.quantidade());

        Produto salvo = produtoRepository.save(produto);

        // Registra a entrada inicial no estoque
        estoqueService.registrarMovimento(salvo.getId(), "ENTRADA", salvo.getQuantidade());

        return toDTO(salvo);
    }

    /**
     * Listar todos os produtos
     */
    public List<ProdutoDTO> listar() {
        return produtoRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Buscar produto por ID
     */
    public Optional<ProdutoDTO> buscarPorId(Long id) {
        return produtoRepository.findById(id)
                .map(this::toDTO);
    }

    /**
     * Buscar modelo de produto por ID (sem conversão para DTO)
     * Usado internamente por outros services
     */
    public Optional<Produto> buscarProdutoModelo(Long id) {
        return produtoRepository.findById(id);
    }

    /**
     * Atualizar produto
     */
    public ProdutoDTO atualizar(Long id, ProdutoRequest request) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        produto.setNome(request.nome());
        produto.setDescricao(request.descricao());
        produto.setPreco(request.preco());
        produto.setUrlFoto(request.urlFoto());
        produto.setQuantidade(request.quantidade());

        Produto atualizado = produtoRepository.save(produto);
        return toDTO(atualizado);
    }

    /**
     * Remover produto
     */
    public void remover(Long id) {
        if (!produtoRepository.existsById(id)) {
            throw new RuntimeException("Produto não encontrado");
        }
        produtoRepository.deleteById(id);
    }

    // ========================================
    // OPERAÇÕES ESPECÍFICAS
    // ========================================

    /**
     * Busca produtos pelo nome (busca parcial, case-insensitive)
     * 
     * @param nome Nome ou parte do nome a buscar
     * @return Lista de DTOs com produtos encontrados
     */
    public List<ProdutoBuscaResponse> buscarPorNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return List.of();
        }

        return produtoRepository.findByNomeContainingIgnoreCase(nome.trim())
                .stream()
                .map(p -> new ProdutoBuscaResponse(
                        p.getId(),
                        p.getNome(),
                        p.getPreco(),
                        p.getQuantidade() != null ? p.getQuantidade() : 0))
                .collect(Collectors.toList());
    }

    /**
     * Validar se há quantidade suficiente em estoque
     * 
     * @param produto    Produto a validar
     * @param quantidade Quantidade desejada
     * @return true se há estoque suficiente, false caso contrário
     */
    public boolean temEstoqueSuficiente(Produto produto, Integer quantidade) {
        if (produto == null || produto.getQuantidade() == null) {
            return false;
        }
        return produto.getQuantidade() >= quantidade;
    }

    /**
     * Atualizar estoque de um produto
     * 
     * @param produto    Produto a atualizar
     * @param quantidade Nova quantidade
     * @return Produto atualizado
     */
    public Produto atualizarEstoque(Produto produto, Integer quantidade) {
        produto.setQuantidade(quantidade);
        return produtoRepository.save(produto);
    }

    // ========================================
    // HELPER
    // ========================================

    /**
     * Converter modelo para DTO
     */
    private ProdutoDTO toDTO(Produto produto) {
        return new ProdutoDTO(
                produto.getId(),
                produto.getNome(),
                produto.getDescricao(),
                produto.getPreco(),
                produto.getUrlFoto(),
                produto.getQuantidade());
    }
}
