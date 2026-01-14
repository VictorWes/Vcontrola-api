package com.vcontrola.vcontrola.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "cartoes_credito")
@Data
public class CartaoCredito {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "O nome do cartão é obrigatório")
    private String nome;

    @NotNull(message = "O limite total é obrigatório")
    @Positive(message = "O limite deve ser um valor positivo")
    private BigDecimal limiteTotal;

    @NotNull(message = "O limite disponível é obrigatório")
    private BigDecimal limiteDisponivel; // Aqui faremos o cálculo de redução no gasto

    @Min(value = 1, message = "O dia de fechamento deve ser entre 1 e 31")
    @Max(value = 31, message = "O dia de fechamento deve ser entre 1 e 31")
    private Integer diaFechamento;

    @Min(value = 1, message = "O dia de vencimento deve ser entre 1 e 31")
    @Max(value = 31, message = "O dia de vencimento deve ser entre 1 e 31")
    private Integer diaVencimento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
}
