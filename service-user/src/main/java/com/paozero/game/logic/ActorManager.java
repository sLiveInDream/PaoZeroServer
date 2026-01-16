package com.paozero.game.logic;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.PoisonPill;
import akka.actor.Props;
import com.paozero.game.logic.actor.PlayerActor;
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

    public void createPlayerActor(String openId, Map<String, String> userInfoMap){
        ActorRef playerActor = actorSystem.actorOf(Props.create(PlayerActor.class, ()->new PlayerActor(openId, userInfoMap)), ActorTypeEnum.PLAYER.getName());
        actorRefMap.put(openId, playerActor);
    }

    public ActorRef getPlayerActor(String openId){
        return actorRefMap.get(openId);
    }

    public void destroyActor(ActorRef actorRef){
        actorRef.tell(PoisonPill.getInstance(),ActorRef.noSender());
    }
}
