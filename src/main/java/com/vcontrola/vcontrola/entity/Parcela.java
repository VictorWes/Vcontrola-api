package com.vcontrola.vcontrola.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "parcelas")
@Data
public class Parcela {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private Integer numeroParcela;

    private BigDecimal valorParcela;

    private LocalDate dataVencimento;

    private boolean paga = false;

    @ManyToOne
    @JoinColumn(name = "compra_id")
    private Compra compra;
}
