package com.vcontrola.vcontrola.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "compras")
@Data
public class Compra {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    private String nome;

    @NotNull
    @Positive
    private BigDecimal valorTotal;

    @NotNull
    @Positive
    private Integer qtdeParcelas;

    @NotNull
    private LocalDate dataCompra;

    @ManyToOne
    @JoinColumn(name = "cartao_id", nullable = false)
    private CartaoCredito cartao;

    @OneToMany(mappedBy = "compra", cascade = CascadeType.ALL)
    private List<Parcela> parcelas;
}
