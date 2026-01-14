package com.vcontrola.vcontrola.entity;

import com.vcontrola.vcontrola.enums.StatusTransacaoCartao;
import com.vcontrola.vcontrola.enums.TipoTransacao;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "transacoes")
@Data
public class Transacao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "A descrição é obrigatória")
    private String descricao;

    @NotNull(message = "O valor é obrigatório")
    @Positive(message = "O valor deve ser maior que zero")
    private BigDecimal valor;

    @NotNull(message = "O tipo de transação é obrigatório")
    @Enumerated(EnumType.STRING)
    private TipoTransacao tipo;

    @NotNull(message = "O status da transação é obrigatório")
    @Enumerated(EnumType.STRING)
    private StatusTransacaoCartao status;

    @NotNull(message = "A data da transação é obrigatória")
    private LocalDate data;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cartao_credito_id") // Pode ser nulo se for gasto em conta
    private CartaoCredito cartaoCredito;

    // O "RG" de família: UUID gerado no Service para agrupar parcelas
    private UUID transactionGroupId;

    // Ex: "1/10", "2/10"
    private String numeroParcela;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conta_id", nullable = false)
    @NotNull(message = "A transação deve estar vinculada a uma conta")
    private Conta conta;
}
