package com.vcontrola.vcontrola.repository;

import com.vcontrola.vcontrola.entity.ItemPlanejamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ItemPlanejamentoRepository extends JpaRepository<ItemPlanejamento, UUID> {
    List<ItemPlanejamento> findByControleId(UUID controleId);
}
