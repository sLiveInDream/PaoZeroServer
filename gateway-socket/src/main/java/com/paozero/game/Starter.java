package com.paozero.game;

import com.paozero.game.config.DubboConfig;
import com.paozero.game.protobuf.Msg;
import com.paozero.game.protobuf.MsgHeader;
import com.paozero.game.protobuf.NotifyService;
import com.paozero.game.redis.dao.ChannelRouterRedisDao;
import com.paozero.game.remote.RemoteDispatcher;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Scanner;

@Slf4j
@EnableDubbo
@SpringBootApplication
public class Starter {
    public static void main(String[] args) {
        SpringApplication.run(Starter.class, args);
        log.info("gateway-socket start...");

        Scanner sc = new Scanner(System.in);
        while (sc.hasNextLine()) {
            String input = sc.nextLine();
            if("a".equals(input)){
                log.info("channelActive");
                ChannelRouterRedisDao.Instance.putRouterAddress("test", NotifyService.class.getName(), DubboConfig.INSTANCE.SERVER_ADDRESS);

            }
            if("r".equals(input)){
                log.info("channelRead");
                RemoteDispatcher.Instance.dispatch("test", Msg.newBuilder().setHeader(MsgHeader.newBuilder().setBusinessId(1).build()).build());
            }
            if("in".equals(input)){
                log.info("channelInActive");
                ChannelRouterRedisDao.Instance.removeRouterAddress("test");
            }
        }

        sc.close();
    }
}
