package com.teste.vr.controller;



import com.fasterxml.jackson.core.JsonProcessingException;
import com.teste.vr.dto.CartaoDto;
import com.teste.vr.dto.TransacaoDto;
import com.teste.vr.model.Cartao;
import com.teste.vr.service.CartaoService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class CartaoController {

    private final CartaoService cartaoService;

    @PostMapping("/cartoes")
    public ResponseEntity<CartaoDto> saveCartao (@RequestBody CartaoDto cartaoDto) throws RuntimeException, JsonProcessingException {
        return cartaoService.saveCartao(cartaoDto);
    }

    @PostMapping("/transacoes")
    public ResponseEntity<String> doTransacao (@RequestBody TransacaoDto transacaoDto) {
        return cartaoService.doTransacao(transacaoDto);
    }

    @GetMapping("/cartoes/{id}")
    public ResponseEntity<Float> getCartao (@PathVariable(value = "id") String numeroCartao) throws RuntimeException {
        return cartaoService.getCartao(numeroCartao);
    }

}
