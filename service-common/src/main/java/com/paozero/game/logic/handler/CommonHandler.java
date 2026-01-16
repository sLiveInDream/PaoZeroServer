package com.paozero.game.logic.handler;

import com.paozero.game.logic.entity.ActorEntity;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommonHandler {
    private ActorEntity entity;
    public CommonHandler(ActorEntity entity) {
        this.entity = entity;
    }

    public void handleStop(String stop) {
        log.info("stop actor! actorEntity:{}", entity.getEntityId());
    }

    public void handelUnknow(Object msg) {
        log.warn("handleUnknow actorEntity:{}, msg:{}", entity.getEntityId(), msg);
    }
}
