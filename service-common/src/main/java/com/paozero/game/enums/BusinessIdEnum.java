package com.paozero.game.enums;

public enum BusinessIdEnum {
    LOGIN(1);

    private int id;
    public int getId() {
        return id;
    }
    BusinessIdEnum(int v){
        id = v;
    }
}
