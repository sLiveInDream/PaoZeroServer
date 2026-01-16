package com.paozero.game.logic;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.PoisonPill;
import akka.actor.Props;
import com.paozero.game.logic.actor.ConnectActor;
import com.paozero.game.logic.enums.ActorTypeEnum;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ActorManager {
    private static final String ACTOR_SYSTEM_NAME = "actorSystem";
    public static ActorManager Instance;
    private ActorSystem actorSystem;
    private Map<String, ActorRef> actorRefMap;


    @PostConstruct
    public void init(){
        Instance = this;
        actorSystem = ActorSystem.create(ACTOR_SYSTEM_NAME);
        actorRefMap = new ConcurrentHashMap<>();
    }

    @PreDestroy
    public void destroy(){
        actorSystem.terminate();
    }

    public void createConnectActor(String channelKey){
        ActorRef actorRef = actorSystem.actorOf(Props.create(ConnectActor.class, ()->new ConnectActor(channelKey)), ActorTypeEnum.CONNECT.name());
        actorRefMap.put(channelKey, actorRef);
    }

    public ActorRef getConnectActor(String channelKey){
        return actorRefMap.get(channelKey);
    }

    public void stopActor(String channelKey){
        ActorRef actorRef = actorRefMap.get(channelKey);
        if(actorRef == null){
            return;
        }

        actorRef.tell(PoisonPill.getInstance(),ActorRef.noSender());
    }
}
