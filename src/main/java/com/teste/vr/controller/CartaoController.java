package com.teste.vr.controller;



import com.fasterxml.jackson.core.JsonProcessingException;
import com.teste.vr.dto.CartaoRespostaDto;
import com.teste.vr.dto.TransacaoDto;
import com.teste.vr.model.Cartao;
import com.teste.vr.service.PessoaService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class CartaoController {

    private final PessoaService pessoaService;

    @PostMapping("/cartoes")
    public ResponseEntity<CartaoRespostaDto> saveCartao (@RequestBody Cartao cartao) throws RuntimeException, JsonProcessingException {
        return pessoaService.saveCartao(cartao);
    }

    @PostMapping("/transacoes")
    public ResponseEntity<String> doTransacao (@RequestBody TransacaoDto transacaoDto) {
        return pessoaService.doTransacao(transacaoDto);
    }

    @GetMapping("/cartoes/{id}")
    public ResponseEntity<Float> getCartao (@PathVariable(value = "id") String numeroCartao) throws RuntimeException {
        return pessoaService.getCartao(numeroCartao);
    }

}
