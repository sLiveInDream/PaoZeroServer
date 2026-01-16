package com.paozero.game.logic.enums;

public enum ActorTypeEnum {
    /**
     * 服务内部的全局Actor
     */
    GLOBAL(0, "globalActor"),
    /**
     * 网关层的连接Actor
     */
    CONNECT(1, "connectActor"),
    /**
     * user服务的玩家Actor
     */
    PLAYER(2, "playerActor")
    ;

    private int type;
    private String name;
    ActorTypeEnum(int v, String n){
        type = v;
        name = n;
    }

    public int getType(){
        return type;
    }

    public String getName(){
        return name;
    }
}
