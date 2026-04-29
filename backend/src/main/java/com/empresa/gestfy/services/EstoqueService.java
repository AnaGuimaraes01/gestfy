package com.empresa.gestfy.services;

import com.empresa.gestfy.dto.estoque.EstoqueDTO;
import com.empresa.gestfy.dto.estoque.EstoqueRequest;
import com.empresa.gestfy.models.Estoque;
import com.empresa.gestfy.repositories.EstoqueRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * EstoqueService
 * Responsável por gerenciar movimentações de estoque.
 * Separa a lógica de negócio da camada HTTP.
 */
@Service
public class EstoqueService {

    private final EstoqueRepository estoqueRepository;

    public EstoqueService(EstoqueRepository estoqueRepository) {
        this.estoqueRepository = estoqueRepository;
    }

    /**
     * Registra uma movimentação de estoque (ENTRADA ou SAÍDA)
     * 
     * @param request EstoqueRequest com dados da movimentação
     * @return DTO com a movimentação registrada
     */
    public EstoqueDTO criar(EstoqueRequest request) {
        Estoque estoque = new Estoque();
        estoque.setProdutoId(request.produtoId());
        estoque.setTipoMovimento(request.tipoMovimento());
        estoque.setQuantidade(request.quantidade());
        estoque.setDataMovimento(LocalDateTime.now());

        Estoque salvo = estoqueRepository.save(estoque);
        return toDTO(salvo);
    }

    /**
     * Registra uma movimentação de estoque (versão simplificada)
     * 
     * @param produtoId     ID do produto
     * @param tipoMovimento "ENTRADA" ou "SAÍDA"
     * @param quantidade    Quantidade movimentada
     * @return Objeto Estoque salvo
     */
    public Estoque registrarMovimento(Long produtoId, String tipoMovimento, Integer quantidade) {
        Estoque movimento = new Estoque();
        movimento.setProdutoId(produtoId);
        movimento.setTipoMovimento(tipoMovimento);
        movimento.setDataMovimento(LocalDateTime.now());
        movimento.setQuantidade(quantidade);
        return estoqueRepository.save(movimento);
    }

    /**
     * Listar todas as movimentações
     */
    public List<EstoqueDTO> listar() {
        return estoqueRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Buscar movimentação por ID
     */
    public Optional<EstoqueDTO> buscarPorId(Long id) {
        return estoqueRepository.findById(id)
                .map(this::toDTO);
    }

    /**
     * Listar movimentações de um produto
     */
    public List<EstoqueDTO> listarPorProduto(Long produtoId) {
        return estoqueRepository.findByProdutoId(produtoId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Filtrar por tipo de movimento
     */
    public List<EstoqueDTO> filtrarPorTipo(String tipo) {
        return estoqueRepository.findAll()
                .stream()
                .filter(e -> e.getTipoMovimento().equalsIgnoreCase(tipo))
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Filtrar por data
     */
    public List<EstoqueDTO> filtrarPorData(String data) {
        LocalDate dataFiltro = LocalDate.parse(data);
        return estoqueRepository.findAll()
                .stream()
                .filter(e -> e.getDataMovimento().toLocalDate().equals(dataFiltro))
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Atualizar movimentação
     */
    public EstoqueDTO atualizar(Long id, EstoqueRequest request) {
        Estoque estoque = estoqueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movimentação não encontrada"));

        estoque.setTipoMovimento(request.tipoMovimento());
        estoque.setQuantidade(request.quantidade());

        Estoque atualizado = estoqueRepository.save(estoque);
        return toDTO(atualizado);
    }

    /**
     * Remover movimentação
     */
    public void remover(Long id) {
        if (!estoqueRepository.existsById(id)) {
            throw new RuntimeException("Movimentação não encontrada");
        }
        estoqueRepository.deleteById(id);
    }

    /**
     * Obter resumo de estoque
     */
    public Map<String, Object> obterResumo() {
        List<Estoque> movimentos = estoqueRepository.findAll();

        Integer totalEntradas = movimentos.stream()
                .filter(e -> "ENTRADA".equals(e.getTipoMovimento()))
                .mapToInt(Estoque::getQuantidade)
                .sum();

        Integer totalSaidas = movimentos.stream()
                .filter(e -> "SAIDA".equals(e.getTipoMovimento()))
                .mapToInt(Estoque::getQuantidade)
                .sum();

        Integer saldoTotal = totalEntradas - totalSaidas;

        Map<String, Object> response = new HashMap<>();
        response.put("totalEntradas", totalEntradas);
        response.put("totalSaidas", totalSaidas);
        response.put("saldoTotal", saldoTotal);
        response.put("totalMovimentacoes", movimentos.size());

        return response;
    }

    /**
     * Converter modelo para DTO
     */
    private EstoqueDTO toDTO(Estoque estoque) {
        return new EstoqueDTO(
                estoque.getId(),
                estoque.getProdutoId(),
                estoque.getTipoMovimento(),
                estoque.getDataMovimento(),
                estoque.getQuantidade());
    }
}
