package com.teste.vr.service;

import com.teste.vr.dto.CartaoRespostaDto;
import com.teste.vr.dto.TransacaoDto;
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

    public ResponseEntity<CartaoRespostaDto> saveCartao (@RequestBody Cartao cartao) throws RuntimeException {
        Optional<Cartao> cartaoById = cartaoRepository.findById(cartao.getNumeroCartao());
        if (cartaoById.isPresent()) {
            log.error(String.format("Cartão com número %s já existente", cartao.getNumeroCartao()));
            return new ResponseEntity<>(CartaoRespostaDto.transformaEmDTO(cartao), HttpStatus.UNPROCESSABLE_ENTITY);
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
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<String> doTransacao (@RequestBody TransacaoDto transacaoDto) {
        Optional<Cartao> cartao = cartaoRepository.findById(transacaoDto.getNumeroCartao());
        if (cartao.isEmpty()) {
            log.error(String.format("Cartão com número %s não encontrado", transacaoDto.getNumeroCartao()));
            return new ResponseEntity<>("CARTAO_INEXISTENTE", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if (!myUtils.checaSeSenhasMatches(transacaoDto.getSenha(),cartao.get().getSenha())) {
            log.error(String.format("Senha inválida para o cartão de número %s", transacaoDto.getNumeroCartao()));
            return new ResponseEntity<>("SENHA_INVALIDA", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if (transacaoDto.getValor() > cartao.get().getSaldo()) {
            log.error("Saldo insuficiente para a transação");
            return new ResponseEntity<>("SALDO_INSUFICIENTE", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        cartao.get().setSaldo(cartao.get().getSaldo() - transacaoDto.getValor());
        cartaoRepository.save(cartao.get());
        return new ResponseEntity<>("OK", HttpStatus.CREATED);
    }


}
