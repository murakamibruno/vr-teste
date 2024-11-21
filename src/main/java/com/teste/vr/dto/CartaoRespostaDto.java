package com.teste.vr.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.teste.vr.model.Cartao;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartaoRespostaDto {

    private String numeroCartao;
    private String senha;

    public static CartaoRespostaDto transformaEmDTO(Cartao cartao) {
        return new CartaoRespostaDto(cartao.getNumeroCartao(), cartao.getSenha());
    }
}
