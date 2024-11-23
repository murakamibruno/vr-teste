package com.teste.vr.dto;

import com.teste.vr.model.Cartao;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartaoDto {

    private String numeroCartao;
    private String senha;

    public static CartaoDto transformaEmDTO(Cartao cartao) {
        return new CartaoDto(cartao.getNumeroCartao(), cartao.getSenha());
    }
}
