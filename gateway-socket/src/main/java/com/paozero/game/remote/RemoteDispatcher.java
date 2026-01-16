package com.paozero.game.remote;

import com.paozero.game.channel.ChannelCache;
import com.paozero.game.protobuf.BusinessId;
import com.paozero.game.protobuf.GameService;
import com.paozero.game.protobuf.Msg;
import com.paozero.game.protobuf.RpcRequest;
import com.paozero.game.protobuf.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.StringUtils;
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

            String openId = ChannelCache.CHANNEL_TO_USER_CACHE.get(channelKey);
            if(StringUtils.isEmpty(openId)){
                if(businessId != BusinessId.LOGIN_VALUE){
                    log.error("RemoteDispatcher dispatch error, channelKey:{} not bind openId, msg:{}",channelKey,msg);
                    return;
                }
            }
            RpcRequest rpcRequest = RpcRequest.newBuilder().setChannelKey(channelKey).setOpenId(openId == null?"":openId).setMsg(msg).build();

            if(businessId > BusinessId.NONE_VALUE && businessId < BusinessId.USER_GAME_BUSINESS_DIVIDE_VALUE){
                userService.dispatchAsync(rpcRequest);
            }
        }catch (Exception e){
            log.error("RemoteDispatcher error channelKey:{}, msg:{}",channelKey,msg);
            log.error(e.getMessage(),e);
        }
    }
}
