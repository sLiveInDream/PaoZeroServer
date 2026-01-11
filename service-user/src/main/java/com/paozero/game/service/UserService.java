package com.paozero.game.service;

import com.paozero.game.protobuf.DubboUserServiceTriple;
import com.paozero.game.protobuf.ErrorCode;
import com.paozero.game.protobuf.LoginResponse;
import com.paozero.game.protobuf.Msg;
import com.paozero.game.protobuf.MsgHeader;
import com.paozero.game.protobuf.NotifyService;
import com.paozero.game.protobuf.RpcRequest;
import com.paozero.game.protobuf.RpcResponse;
import com.paozero.game.enums.BusinessIdEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@DubboService
public class UserService extends DubboUserServiceTriple.UserServiceImplBase {
    @DubboReference
    private NotifyService notifyService;

    @Override
    public CompletableFuture<RpcResponse> dispatchAsync(RpcRequest request) {
        return  CompletableFuture.supplyAsync(() -> {
            //todo 直接把消息传到对应的actor
            try{
                Msg msg = Msg.parseFrom(request.getMsg());
                if(msg.getHeader().getBusinessId() == BusinessIdEnum.LOGIN.getId()){
                    long userId = 1;
                    MsgHeader msgHeader = MsgHeader.newBuilder().setCode(ErrorCode.SUCCESS_VALUE).setBusinessId(msg.getHeader().getBusinessId()).build();
                    LoginResponse loginResponse = LoginResponse.newBuilder().setUserId(userId).build();
                    Msg loginResMsg = Msg.newBuilder().setHeader(msgHeader).setBody(loginResponse.toByteString()).build();
                    notifyService.dispatchAsync(RpcRequest.newBuilder().setChannelKey(request.getChannelKey()).setUserId(userId).setMsg(loginResMsg.toByteString()).build());
                }
            }catch (Exception e){
                log.error("UserService dispatchAsync error!",e);
            }

            return RpcResponse.newBuilder().setCode(ErrorCode.SUCCESS_VALUE).build();
        });
    }
}
