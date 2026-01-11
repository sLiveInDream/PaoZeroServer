package com.paozero.game.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class DubboConfig {
    public static DubboConfig INSTANCE;

    @PostConstruct
    public void init() {
        INSTANCE = this;
    }

    @Value("${dubbo.gateway.serverAddress}")
    public String SERVER_ADDRESS;
}
