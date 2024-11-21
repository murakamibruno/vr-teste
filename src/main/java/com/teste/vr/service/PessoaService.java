package com.teste.vr.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.teste.vr.dto.CartaoRespostaDto;
import com.teste.vr.dto.TransacaoDto;
import com.teste.vr.exception.CartaoInexistenteException;
import com.teste.vr.exception.CartaoNotFoundException;
import com.teste.vr.exception.TransacaoException;
import com.teste.vr.model.Cartao;
import com.teste.vr.repository.CartaoRepository;
import com.teste.vr.utils.MyUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class PessoaService {

    private final CartaoRepository cartaoRepository;

    private MyUtils myUtils;

    public ResponseEntity<CartaoRespostaDto> saveCartao (@RequestBody Cartao cartao) throws RuntimeException, JsonProcessingException {
        Optional<Cartao> cartaoById = cartaoRepository.findById(cartao.getNumeroCartao());
        if (cartaoById.isPresent()) {
            log.error(String.format("Cartão com número %s já existente", cartao.getNumeroCartao()));
            throw new CartaoInexistenteException(CartaoRespostaDto.transformaEmDTO(cartao));
        }
        cartao.setSaldo(500);
        cartao.setSenha(myUtils.encodeSenha(cartao.getSenha()));
        Cartao cartaoCreated = cartaoRepository.save(cartao);
        log.info(String.format("Cartão de número %s criado com sucesso", cartao.getNumeroCartao()));
        return new ResponseEntity<>(CartaoRespostaDto.transformaEmDTO(cartaoCreated), HttpStatus.CREATED);
    }

    public ResponseEntity<Float> getCartao (@PathVariable(value = "id") String numeroCartao) {
        Optional<Cartao> cartaoById = cartaoRepository.findById(numeroCartao);
        if (cartaoById.isPresent()) {
            return new ResponseEntity<Float>(cartaoById.get().getSaldo(), HttpStatus.OK);
        }
        log.error(String.format("Cartão com número %s não encontrado", numeroCartao));
        throw new CartaoNotFoundException();
    }

    public ResponseEntity<String> doTransacao (@RequestBody TransacaoDto transacaoDto) {
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
