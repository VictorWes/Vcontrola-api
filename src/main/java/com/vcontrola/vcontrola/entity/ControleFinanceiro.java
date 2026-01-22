package com.vcontrola.vcontrola.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "controle_financeiro")
@Data
public class ControleFinanceiro {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private BigDecimal saldoDisponivel = BigDecimal.ZERO;

    @OneToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
}
