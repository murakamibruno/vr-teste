package com.teste.vr.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.teste.vr.dto.CartaoDto;
import com.teste.vr.dto.TransacaoDto;
import com.teste.vr.exception.CartaoJaExistenteException;
import com.teste.vr.exception.CartaoNotFoundException;
import com.teste.vr.exception.TransacaoException;
import com.teste.vr.model.Cartao;
import com.teste.vr.repository.CartaoRepository;
import com.teste.vr.utils.MyUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class CartaoService {

    @Autowired
    private CartaoRepository cartaoRepository;

    @Autowired
    private MyUtils myUtils;

    private static final float SALDO_INICIAL = 500;

    public ResponseEntity<CartaoDto> saveCartao (@RequestBody CartaoDto cartaoDto) throws RuntimeException, JsonProcessingException {
        Optional<Cartao> cartaoById = cartaoRepository.findById(cartaoDto.getNumeroCartao());
        if (cartaoById.isPresent()) {
            log.error(String.format("Cartão com número %s já existente", cartaoDto.getNumeroCartao()));
            throw new CartaoJaExistenteException(cartaoDto);
        }
        Cartao cartao = new Cartao(cartaoDto.getNumeroCartao(), myUtils.encodeSenha(cartaoDto.getSenha()), SALDO_INICIAL);
        Cartao cartaoCreated = cartaoRepository.save(cartao);
        log.info(String.format("Cartão de número %s criado com sucesso", cartao.getNumeroCartao()));
        return new ResponseEntity<>(CartaoDto.transformaEmDTO(cartaoCreated), HttpStatus.CREATED);
    }

    public ResponseEntity<Float> getCartao (@PathVariable(value = "id") String numeroCartao) {
        Optional<Cartao> cartaoById = cartaoRepository.findById(numeroCartao);
        if (cartaoById.isPresent()) {
            return new ResponseEntity<Float>(cartaoById.get().getSaldo(), HttpStatus.OK);
        }
        log.error(String.format("Cartão com número %s não encontrado", numeroCartao));
        throw new CartaoNotFoundException();
    }

    public synchronized ResponseEntity<String> doTransacao (@RequestBody TransacaoDto transacaoDto) {
        Optional<Cartao> cartao = cartaoRepository.findById(transacaoDto.getNumeroCartao());
        if (cartao.isEmpty()) {
            log.error(String.format("Cartão com número %s não encontrado", transacaoDto.getNumeroCartao()));
            throw new TransacaoException("CARTAO_INEXISTENTE");
        }
        if (!myUtils.checaSeSenhasMatches(transacaoDto.getSenha(),cartao.get().getSenha())) {
            log.error(String.format("Senha inválida para o cartão de número %s", transacaoDto.getNumeroCartao()));
            throw new TransacaoException("SENHA_INVALIDA");
        }
        if (transacaoDto.getValor() > cartao.get().getSaldo()) {
            log.error("Saldo insuficiente para a transação");
            throw new TransacaoException("SALDO_INSUFICIENTE");
        }
        cartao.get().setSaldo(cartao.get().getSaldo() - transacaoDto.getValor());
        cartaoRepository.save(cartao.get());
        return new ResponseEntity<>("OK", HttpStatus.CREATED);
    }
}
