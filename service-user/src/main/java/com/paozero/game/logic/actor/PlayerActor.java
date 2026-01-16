package com.paozero.game.logic.actor;

import akka.actor.AbstractActor;
import com.paozero.game.constant.RedisUserInfoKeyDefine;
import com.paozero.game.logic.component.PlayerInfoComponent;
import com.paozero.game.logic.entity.ActorEntity;
import com.paozero.game.logic.handler.CommonHandler;
import com.paozero.game.logic.handler.PlayerInfoHandler;
import com.paozero.game.protobuf.Msg;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class PlayerActor extends AbstractActor {
    private ActorEntity entity;
    private PlayerInfoHandler playerInfoHandler;
    private CommonHandler commonHandler;

    public PlayerActor(String openId, Map<String, String> userInfoMap){
        log.info("PlayerActor constructor");
        entity = new ActorEntity();
        PlayerInfoComponent playerInfoComponent = entity.addComponent(PlayerInfoComponent.class);
        playerInfoComponent.setOpenId(openId);
        playerInfoComponent.setNickName(userInfoMap.get(RedisUserInfoKeyDefine.USER_INFO_NICKNAME));
        playerInfoHandler = new PlayerInfoHandler(entity);
        commonHandler = new CommonHandler(entity);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(Msg.class, playerInfoHandler::handleMsg)
                .matchEquals("stop", commonHandler::handleStop).
                matchAny(commonHandler::handelUnknow).build();
    }
}
