package com.paozero.game.service;

import akka.actor.ActorRef;
import com.paozero.game.constant.RedisUserInfoKeyDefine;
import com.paozero.game.logic.ActorManager;
import com.paozero.game.protobuf.BusinessId;
import com.paozero.game.protobuf.DubboUserServiceTriple;
import com.paozero.game.protobuf.ErrorCode;
import com.paozero.game.protobuf.LoginRequest;
import com.paozero.game.protobuf.LoginResponse;
import com.paozero.game.protobuf.Msg;
import com.paozero.game.protobuf.MsgHeader;
import com.paozero.game.protobuf.NotifyService;
import com.paozero.game.protobuf.RpcRequest;
import com.paozero.game.protobuf.RpcResponse;
import com.paozero.game.redis.dao.UserInfoRedisDao;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
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
            int businessId = request.getMsg().getHeader().getBusinessId();
            try{
                if(businessId == BusinessId.LOGIN_VALUE){
                    LoginRequest loginRequest = LoginRequest.parseFrom(request.getMsg().getBody());
                    String openId = getOpenIdByToken(loginRequest.getToken());

                    //从库中读取用户数据或初始化
                    Map<String, String> userInfoMap = UserInfoRedisDao.Instance.getUserInfo(openId);
                    if(userInfoMap == null || userInfoMap.isEmpty()){
                        //创建新用户
                        userInfoMap = new HashMap<>();
                        openId = generateNewOpenId();
                        userInfoMap.put(RedisUserInfoKeyDefine.USER_INFO_OPENID, openId);
                        long now = System.currentTimeMillis();
                        String nickName = "Player_" + now;
                        userInfoMap.put(RedisUserInfoKeyDefine.USER_INFO_NICKNAME, nickName);
                        userInfoMap.put(RedisUserInfoKeyDefine.USER_INFO_CREATE_TIME, String.valueOf(now));
                        UserInfoRedisDao.Instance.setUserInfo(openId, userInfoMap);
                    }

                    //创建actor
                    ActorRef playerActor = ActorManager.Instance.getPlayerActor(openId);
                    if(playerActor != null){
                        ActorManager.Instance.destroyActor(playerActor);
                    }
                    ActorManager.Instance.createPlayerActor(openId, userInfoMap);

                    //发送登录成功响应
                    MsgHeader msgHeader = MsgHeader.newBuilder().setCode(ErrorCode.SUCCESS_VALUE).setBusinessId(businessId).build();
                    LoginResponse loginResponse = LoginResponse.newBuilder().setOpenId(openId).setNickName(userInfoMap.get(RedisUserInfoKeyDefine.USER_INFO_NICKNAME)).build();
                    Msg loginResMsg = Msg.newBuilder().setHeader(msgHeader).setBody(loginResponse.toByteString()).build();
                    notifyService.dispatchAsync(RpcRequest.newBuilder().setChannelKey(request.getChannelKey()).setOpenId(openId).setMsg(loginResMsg).build());
                }else {
                    String openId = request.getOpenId();
                    if(StringUtils.isEmpty(openId)){
                        log.error("UserService dispatchAsync error! openId is empty for request:{}", request);
                        return RpcResponse.newBuilder().setCode(ErrorCode.NOT_LOGIN_VALUE).setBusinessId(businessId).build();
                    }
                    ActorRef playerActor = ActorManager.Instance.getPlayerActor(openId);
                    if(playerActor == null){
                        log.error("UserService dispatchAsync error! playerActor is empty for request:{}", request);
                        return RpcResponse.newBuilder().setCode(ErrorCode.NOT_LOGIN_VALUE).setBusinessId(businessId).build();
                    }
                    playerActor.tell(request.getMsg(), ActorRef.noSender());
                }
            }catch (Exception e){
                log.error("UserService dispatchAsync error!",e);
                return RpcResponse.newBuilder().setCode(ErrorCode.NOT_LOGIN_VALUE).setBusinessId(businessId).build();
            }

            return RpcResponse.newBuilder().setCode(ErrorCode.SUCCESS_VALUE).setBusinessId(businessId).build();
        });
    }

    private String getOpenIdByToken(String token){
        //TODO 从缓存或者数据库获取openid
        return "openId_test";
    }

    private String generateNewOpenId(){
        return UUID.randomUUID().toString().replace("-", "");
    }
}
