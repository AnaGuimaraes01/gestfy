package com.empresa.gestfy.controllers;

import com.empresa.gestfy.dto.pedido.PedidoDTO;
import com.empresa.gestfy.dto.pedido.PedidoRequest;
import com.empresa.gestfy.models.Pedido;
import com.empresa.gestfy.repositories.PedidoRepository;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoRepository repository;

    public PedidoController(PedidoRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public PedidoDTO criar(@Valid @RequestBody PedidoRequest request) {
        Pedido pedido = new Pedido(
                request.nomeCliente(),
                request.telefone(),
                request.formaPagamento(),
                request.status(),
                request.total()
        );

        Pedido salvo = repository.save(pedido);

        return new PedidoDTO(
                salvo.getId(),
                salvo.getNomeCliente(),
                salvo.getTelefone(),
                salvo.getFormaPagamento(),
                salvo.getStatus(),
                salvo.getTotal(),
                salvo.getData()
        );
    }

    @GetMapping
    public List<PedidoDTO> listar() {
        return repository.findAll().stream().map(p ->
                new PedidoDTO(
                        p.getId(),
                        p.getNomeCliente(),
                        p.getTelefone(),
                        p.getFormaPagamento(),
                        p.getStatus(),
                        p.getTotal(),
                        p.getData()
                )
        ).toList();
    }
}
