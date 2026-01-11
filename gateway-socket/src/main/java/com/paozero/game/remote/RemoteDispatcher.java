package com.paozero.game.remote;

import com.paozero.game.protobuf.GameService;
import com.paozero.game.protobuf.Msg;
import com.paozero.game.protobuf.RpcRequest;
import com.paozero.game.protobuf.UserService;
import com.paozero.game.enums.BusinessIdEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@Component
public class RemoteDispatcher {
    public static RemoteDispatcher Instance;
    @DubboReference
    private UserService userService;
    @DubboReference
    private GameService gameService;

    @PostConstruct
    public void init(){
        Instance = this;
    }

    public void dispatch(String channelKey, Msg msg) {

        try {
            int businessId = msg.getHeader().getBusinessId();
            RpcRequest rpcRequest = RpcRequest.newBuilder().setChannelKey(channelKey).setUserId(0).setMsg(msg.toByteString()).build();

            if(businessId == BusinessIdEnum.LOGIN.getId()){
                userService.dispatchAsync(rpcRequest);
            }
        }catch (Exception e){
            log.error("RemoteDispatcher error channelKey:{}, msg:{}",channelKey,msg);
        }
    }
}
