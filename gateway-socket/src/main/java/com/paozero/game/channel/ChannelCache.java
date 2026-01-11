package com.paozero.game.channel;

import io.netty.channel.ChannelHandlerContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChannelCache {
    //channelKey -> ChannelHandlerContext
    public static Map<String, ChannelHandlerContext> CONTEXT_CACHE = new ConcurrentHashMap<>();
    // channelKey -> userId
    public static Map<String, Long> CHANNEL_TO_USER_CACHE = new ConcurrentHashMap<>();
    // userId -> channelKey
    public static Map<Long, String> USER_TO_CHANNEL_CACHE = new ConcurrentHashMap<>();

}
