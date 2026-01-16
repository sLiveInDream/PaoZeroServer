package com.paozero.game.logic.actor;

import akka.actor.AbstractActor;
import com.paozero.game.protobuf.Msg;
import com.paozero.game.remote.RemoteDispatcher;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConnectActor extends AbstractActor {

    private final String channelKey;

    public ConnectActor(String channelKey) {
        log.info("ConnectActor constructor channelKey:{}",channelKey);
        this.channelKey = channelKey;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(Msg.class, this::handleMsg)
                        .matchAny(this::handelUnknow).build();
    }

    private void handleMsg(Msg msg) {
        RemoteDispatcher.Instance.dispatch(channelKey, msg);
    }

    private void handelUnknow(Object msg) {
        log.warn("handleUnknow channelKey:{}, msg:{}", channelKey, msg);
    }
}
