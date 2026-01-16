package com.paozero.game.logic.handler;

import com.paozero.game.logic.component.PlayerInfoComponent;
import com.paozero.game.logic.entity.ActorEntity;
import com.paozero.game.protobuf.Msg;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class PlayerInfoHandler {
    private ActorEntity entity;
    public PlayerInfoHandler(ActorEntity entity) {
        this.entity = entity;
    }

    public void handleMsg(Msg msg) {
        log.info("PlayerInfoHandler handleMsg msg:{}", msg);
        PlayerInfoComponent playerInfoComponent =  entity.getComponentData(PlayerInfoComponent.class);
        if(playerInfoComponent == null){
            return;
        }
    }
}
