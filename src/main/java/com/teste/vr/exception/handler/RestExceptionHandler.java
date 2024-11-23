package com.teste.vr.exception.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teste.vr.dto.CartaoDto;
import com.teste.vr.exception.CartaoJaExistenteException;
import com.teste.vr.exception.CartaoNotFoundException;
import com.teste.vr.exception.TransacaoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CartaoJaExistenteException.class)
    private ResponseEntity<CartaoDto> cartaoJaExistenteHandler (CartaoJaExistenteException exception) throws JsonProcessingException {
        return new ResponseEntity<>(new ObjectMapper().readValue(exception.getMessage(), CartaoDto.class), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(CartaoNotFoundException.class)
    private ResponseEntity<String> cartaoNotFoundHandler() {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TransacaoException.class)
    private ResponseEntity<String> transacaoHandler(TransacaoException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
