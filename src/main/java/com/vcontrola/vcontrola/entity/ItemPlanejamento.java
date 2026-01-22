package com.vcontrola.vcontrola.entity;

import com.vcontrola.vcontrola.enums.StatusPlanejamento;
import com.vcontrola.vcontrola.enums.TipoConta;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "itens_planejamento")
@Data
public class ItemPlanejamento {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "carteira_id")
    private TipoContaUsuario carteira;

    private BigDecimal valor; // Ex: 50.00

    @Enumerated(EnumType.STRING)
    private StatusPlanejamento status;

    @ManyToOne
    @JoinColumn(name = "conta_destino_id")
    private Conta contaDestino;

    @ManyToOne
    @JoinColumn(name = "controle_id")
    private ControleFinanceiro controle;
}
