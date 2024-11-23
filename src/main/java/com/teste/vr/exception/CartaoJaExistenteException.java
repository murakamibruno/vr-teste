package com.teste.vr.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teste.vr.dto.CartaoDto;

public class CartaoJaExistenteException extends RuntimeException {

    public CartaoJaExistenteException(CartaoDto cartaoResponseDto) throws JsonProcessingException {
        super(new ObjectMapper().writeValueAsString(cartaoResponseDto));
    }
}
