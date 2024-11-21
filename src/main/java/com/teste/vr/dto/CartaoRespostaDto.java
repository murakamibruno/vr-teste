package com.teste.vr.dto;

import com.teste.vr.model.Cartao;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public class CartaoRespostaDto {

    private String numeroCartao;
    private String senha;

    public static CartaoRespostaDto transformaEmDTO(Cartao cartao) {
        return new CartaoRespostaDto(cartao.getNumeroCartao(), cartao.getSenha());
    }
}
