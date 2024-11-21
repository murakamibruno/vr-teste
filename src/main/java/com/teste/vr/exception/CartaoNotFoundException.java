package com.teste.vr.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teste.vr.dto.CartaoRespostaDto;

public class CartaoNotFoundException extends RuntimeException {
    public CartaoNotFoundException() {
        super();
    }
}
