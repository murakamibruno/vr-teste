package com.teste.vr.dto;

import com.teste.vr.model.Cartao;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TransacaoDto {

    private String numeroCartao;
    private String senha;
    private float valor;

}
