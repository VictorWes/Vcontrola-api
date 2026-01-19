package com.vcontrola.vcontrola.entity;

import com.vcontrola.vcontrola.enums.TipoConta;
import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "tipo_conta_usuarios")
@Data
public class TipoContaUsuario {


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String nome;

    private String icone;

    private String cor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoConta comportamento;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

}
