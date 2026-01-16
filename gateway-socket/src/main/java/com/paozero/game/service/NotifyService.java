package com.paozero.game.service;

import com.paozero.game.channel.ChannelCache;
import com.paozero.game.protobuf.BusinessId;
import com.paozero.game.protobuf.DubboNotifyServiceTriple;
import com.paozero.game.protobuf.ErrorCode;
import com.paozero.game.protobuf.LoginResponse;
import com.paozero.game.protobuf.Msg;
import com.paozero.game.protobuf.RpcRequest;
import com.paozero.game.protobuf.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.StringUtils;
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
            try {
                Msg msg = request.getMsg();
                if(msg.getHeader().getBusinessId() == BusinessId.LOGIN_VALUE){
                    LoginResponse loginResponse = LoginResponse.parseFrom(msg.getBody());
                    if(StringUtils.isEmpty(loginResponse.getOpenId())){
                        log.error("NotifyService dispatchAsync login response openId is empty! request:{}", request);
                        return RpcResponse.newBuilder().setCode(ErrorCode.SYSTEM_ERROR_VALUE).build();
                    }
                    ChannelCache.CHANNEL_TO_USER_CACHE.put(request.getChannelKey(), loginResponse.getOpenId());
                    ChannelCache.USER_TO_CHANNEL_CACHE.put(loginResponse.getOpenId(), request.getChannelKey());
                }

                ChannelHandlerContext channelHandlerContext = ChannelCache.CONTEXT_CACHE.get(request.getChannelKey());
                channelHandlerContext.channel().writeAndFlush(request.getMsg());
            }catch (Exception e){
                log.error("NotifyService dispatchAsync error", e);
            }

            return RpcResponse.newBuilder().setCode(ErrorCode.SUCCESS_VALUE).build();
        });
    }
}
