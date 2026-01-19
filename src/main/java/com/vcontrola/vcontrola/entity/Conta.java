package com.vcontrola.vcontrola.entity;

import com.vcontrola.vcontrola.enums.TipoConta;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "contas")
@Data
public class Conta {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "O nome da conta é obrigatório")
    private String nome;

    @NotNull(message = "O saldo inicial deve ser informado")
    @PositiveOrZero(message = "O saldo não pode ser negativo")
    private BigDecimal saldo;

    @ManyToOne
    @JoinColumn(name = "tipo_conta_id")
    private TipoContaUsuario tipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @NotNull(message = "A conta deve pertencer a um usuário")
    private Usuario usuario;
}
