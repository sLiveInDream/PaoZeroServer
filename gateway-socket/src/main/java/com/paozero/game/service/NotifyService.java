package com.paozero.game.service;

import com.paozero.game.protobuf.DubboNotifyServiceTriple;
import com.paozero.game.protobuf.ErrorCode;
import com.paozero.game.protobuf.RpcRequest;
import com.paozero.game.protobuf.RpcResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@DubboService
public class NotifyService extends DubboNotifyServiceTriple.NotifyServiceImplBase {

    @Override
    public CompletableFuture<RpcResponse> dispatchAsync(RpcRequest request){
        return CompletableFuture.supplyAsync(() ->{
            log.info("NotifyService run");
            return RpcResponse.newBuilder().setCode(ErrorCode.SUCCESS_VALUE).build();
        });
    }
}
