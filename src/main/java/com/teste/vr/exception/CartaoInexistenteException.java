package com.teste.vr.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teste.vr.dto.CartaoRespostaDto;
import org.springframework.http.ResponseEntity;

public class CartaoInexistenteException extends RuntimeException {

    public CartaoInexistenteException(CartaoRespostaDto cartaoResponseDto) throws JsonProcessingException {
        super(new ObjectMapper().writeValueAsString(cartaoResponseDto));
    }
}
