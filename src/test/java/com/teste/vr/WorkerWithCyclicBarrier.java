package com.teste.vr;

import com.teste.vr.dto.TransacaoDto;
import com.teste.vr.service.CartaoService;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class WorkerWithCyclicBarrier extends Thread {


    private CartaoService cartaoService;

    private CyclicBarrier barrier;

    private TransacaoDto transacaoDto;

    public WorkerWithCyclicBarrier(String name, CyclicBarrier  barrier, TransacaoDto transacaoDto, CartaoService cartaoService) {
        this.barrier = barrier;
        this.transacaoDto = transacaoDto;
        this.cartaoService = cartaoService;
        setName(name);
    }

    @Override
    public void run() {
        try {
            cartaoService.doTransacao(transacaoDto);
            barrier.await();
        }catch (BrokenBarrierException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}