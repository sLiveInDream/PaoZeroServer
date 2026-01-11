package com.paozero.game.channel;

import com.paozero.game.config.NettyConfig;
import io.netty.channel.Channel;

public class ChannelUtil {
    public static String getChannelKey(Channel channel){
        return NettyConfig.INSTANCE.SERVER_ID + "-" + channel.id().asLongText();
    }
}
