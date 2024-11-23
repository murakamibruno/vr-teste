package com.teste.vr;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teste.vr.dto.CartaoDto;
import com.teste.vr.dto.TransacaoDto;
import com.teste.vr.exception.CartaoJaExistenteException;
import com.teste.vr.exception.CartaoNotFoundException;
import com.teste.vr.exception.TransacaoException;
import com.teste.vr.repository.CartaoRepository;
import com.teste.vr.service.CartaoService;
import com.teste.vr.utils.MyUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class CartaoServiceTests {

    @Autowired
    private CartaoService cartaoService;

    @Autowired
    private CartaoRepository cartaoRepository;

    @Autowired
    private MyUtils myUtils;

    @Autowired
    MockMvc mockMvc;

    CartaoDto cartaoDto;

    String numeroCartao;

    String senhaCartao;

    float valor;

    private static final float SALDO_INICIAL = 500;

    @BeforeEach
    public void setup() {
        numeroCartao = "6549873025634501";
        senhaCartao = "1234";
        cartaoDto = new CartaoDto(numeroCartao, senhaCartao);
        valor = 10;
    }

    @AfterEach
    public void clearData() {
        cartaoRepository.deleteAll();
    }

    @Test
    @DisplayName("Cartão deve ser salvo com sucesso")
    @Order(1)
    public void salvarCartao() throws JsonProcessingException {
        ResponseEntity<CartaoDto> cartaoResponse = cartaoService.saveCartao(cartaoDto);
        assertEquals(cartaoResponse.getStatusCode(), HttpStatus.CREATED);
        assertEquals(cartaoResponse.getBody().getNumeroCartao(), numeroCartao);
        assertTrue(myUtils.checaSeSenhasMatches(senhaCartao, cartaoResponse.getBody().getSenha()));
    }

    @Test
    @DisplayName("Cartão com número duplicado deve lançar exceção de cartão já existente")
    public void testaSeAoSalvarCartaoDuplicadoLancaExcecao() throws JsonProcessingException {
        cartaoService.saveCartao(cartaoDto);
        assertThrows(CartaoJaExistenteException.class, () -> {
            cartaoService.saveCartao(cartaoDto);
        });
    }

    @Test
    @DisplayName("Saldo deve ser mostrado com sucesso")
    public void obterSaldo() throws JsonProcessingException {
        cartaoService.saveCartao(cartaoDto);
        ResponseEntity<Float> response = cartaoService.getCartao(numeroCartao);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody().floatValue(), 500, 0);
    }

    @Test
    @DisplayName("Ao buscar cartão que o número não existe, deve lançar exceção de número não encontrado")
    public void testaSeAoBuscarCartaoComNumeroInexistenteLancaExcecao() throws JsonProcessingException {
        cartaoService.saveCartao(cartaoDto);
        assertThrows(CartaoNotFoundException.class, () -> {
            cartaoService.getCartao("1234");
        });
    }

    @Test
    @DisplayName("Deve realizar a transação e validar o saldo com sucesso")
    public void realizarTransacaoComSucessoEValidarSeSaldoCorreto() throws JsonProcessingException {
        cartaoService.saveCartao(cartaoDto);
        TransacaoDto transacaoDto = new TransacaoDto(numeroCartao, senhaCartao, valor);
        ResponseEntity<String> response = cartaoService.doTransacao(transacaoDto);
        ResponseEntity<Float> responseSaldo = cartaoService.getCartao(numeroCartao);
        assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        assertEquals(response.getBody(), "OK");
        assertEquals(responseSaldo.getBody().floatValue(), SALDO_INICIAL - valor, 0);
    }

    @Test
    @DisplayName("Deve validar se transação com número de cartão errado lança a exceção de cartão inexistente.")
    public void checaSeTransacaoComNumeroCartaoErradoLancaExcecao() throws JsonProcessingException {
        cartaoService.saveCartao(cartaoDto);
        TransacaoDto transacaoDto = new TransacaoDto("123145", senhaCartao, valor);
        Exception response = assertThrows(TransacaoException.class, () -> {
            cartaoService.doTransacao(transacaoDto);
        });
        assertEquals(response.getMessage(), "CARTAO_INEXISTENTE");
    }

    @Test
    @DisplayName("Deve validar se transação com senha errada lança a exceção de senha inválida")
    public void checaSeTransacaoComSenhaErradaLancaExcecao() throws JsonProcessingException {
        cartaoService.saveCartao(cartaoDto);
        TransacaoDto transacaoDto = new TransacaoDto(numeroCartao, "12345", valor);
        Exception response = assertThrows(TransacaoException.class, () -> {
            cartaoService.doTransacao(transacaoDto);
        });
        assertEquals(response.getMessage(), "SENHA_INVALIDA");
    }

    @Test
    @DisplayName("Deve validar se transação com valor maior que o saldo lança exceção de saldo inválido")
    public void checaSeTransacaoComValorMaiorQueSaldoLancaExcecao() throws JsonProcessingException {
        cartaoService.saveCartao(cartaoDto);
        TransacaoDto transacaoDto = new TransacaoDto(numeroCartao, senhaCartao, 510);
        Exception response = assertThrows(TransacaoException.class, () -> {
            cartaoService.doTransacao(transacaoDto);
        });
        assertEquals(response.getMessage(), "SALDO_INSUFICIENTE");
    }

    @Test
    @DisplayName("Testa se cria cartão com sucesso ao utilizar usuário autenticado")
    public void testaAutenticacaoPostCartao() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/cartoes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(cartaoDto))
            .with(SecurityMockMvcRequestPostProcessors.httpBasic("username","password"))
        ).andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    @DisplayName("Caso ocorra erro de autenticação ao salvar cartão, sistema irá lançar erro 401.")
    public void testaAutenticacaoErradaPostCartao() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/cartoes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(cartaoDto))
            .with(SecurityMockMvcRequestPostProcessors.httpBasic("username1","password"))
        ).andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    @DisplayName("Testa se busca saldo com sucesso ao utilizar usuário autenticado")
    public void testaAutenticacaoGetSaldo() throws Exception {
        cartaoService.saveCartao(cartaoDto);
        mockMvc.perform(MockMvcRequestBuilders.get("/cartoes/{id}", numeroCartao)
            .with(SecurityMockMvcRequestPostProcessors.httpBasic("username","password"))
        ).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("Caso ocorra erro de autenticação ao buscar saldo, sistema irá lançar erro 401.")
    public void testaAutenticacaoErradaSaldo() throws Exception {
        cartaoService.saveCartao(cartaoDto);
        mockMvc.perform(MockMvcRequestBuilders.get("/cartoes/{id}", numeroCartao)
            .with(SecurityMockMvcRequestPostProcessors.httpBasic("username","password1"))
        ).andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    @DisplayName("Testa se transação ocorre com sucesso ao utilizar usuário autenticado")
    public void testaAutenticacaoPostTransacao() throws Exception {
        cartaoService.saveCartao(cartaoDto);
        TransacaoDto transacaoDto = new TransacaoDto(numeroCartao, senhaCartao, valor);
        mockMvc.perform(MockMvcRequestBuilders.post("/transacoes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(transacaoDto))
            .with(SecurityMockMvcRequestPostProcessors.httpBasic("username","password"))
        ).andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    @DisplayName("Caso ocorra erro de autenticação na transação, sistema irá lançar erro 401.")
    public void testaAutenticacaoErradaPostTransacao() throws Exception {
        cartaoService.saveCartao(cartaoDto);
        TransacaoDto transacaoDto = new TransacaoDto(numeroCartao, senhaCartao, valor);
        mockMvc.perform(MockMvcRequestBuilders.post("/transacoes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(transacaoDto))
            .with(SecurityMockMvcRequestPostProcessors.httpBasic("username1","password1"))
        ).andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    @DisplayName("Testa se recurso ficará lockado com transações simultâneas")
    public void testaTransacaoThreadsSimultaneasSucesso() throws JsonProcessingException, InterruptedException, BrokenBarrierException, TimeoutException {
        cartaoService.saveCartao(cartaoDto);
        TransacaoDto transacaoDto = new TransacaoDto(numeroCartao, senhaCartao, 10);
        CyclicBarrier barrier = new CyclicBarrier(3);

        WorkerWithCyclicBarrier worker1 = new WorkerWithCyclicBarrier("Worker with barrier 1", barrier, transacaoDto, cartaoService);
        WorkerWithCyclicBarrier worker2 = new WorkerWithCyclicBarrier("Worker with barrier 2", barrier, transacaoDto, cartaoService);

        worker1.start();
        worker2.start();
        barrier.await(5, TimeUnit.SECONDS);

        assertEquals(480, cartaoService.getCartao(numeroCartao).getBody().floatValue(), 0);
    }
}
