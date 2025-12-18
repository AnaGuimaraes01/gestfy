package com.empresa.gestfy.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.empresa.gestfy.models.PedidoItem;
import java.util.List;

public interface PedidoItemRepository extends JpaRepository<PedidoItem, Long> {
    @Query("SELECT pi FROM PedidoItem pi WHERE pi.produto.id = :produtoId")
    List<PedidoItem> findByProdutoId(@Param("produtoId") Long produtoId);
}
