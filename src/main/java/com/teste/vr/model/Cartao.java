package com.teste.vr.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import javax.validation.constraints.NotNull;

@Entity
@Table(name="cartao")
@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@NoArgsConstructor
public class Cartao {

    @Id
    @Column(name="numeroCartao")
    private String numeroCartao;

    @Column(name="senha")
    @NotNull
    private String senha;

    @Column(name="saldo")
    @NotNull
    private float saldo;

}
