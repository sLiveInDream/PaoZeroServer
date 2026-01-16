package com.paozero.game;

import akka.actor.ActorRef;
import com.paozero.game.logic.ActorManager;
import com.paozero.game.channel.ChannelCache;
import com.paozero.game.channel.ChannelUtil;
import com.paozero.game.config.DubboConfig;
import com.paozero.game.protobuf.Msg;
import com.paozero.game.protobuf.NotifyService;
import com.paozero.game.redis.dao.ChannelRouterRedisDao;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.StringUtils;

@Slf4j
public class ServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String channelKey = ChannelUtil.getChannelKey(ctx.channel());
        ChannelCache.CONTEXT_CACHE.put(channelKey, ctx);
        ActorManager.Instance.createConnectActor(channelKey);
        ChannelRouterRedisDao.Instance.putRouterAddress(channelKey, NotifyService.class.getName(), DubboConfig.INSTANCE.SERVER_ADDRESS);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        try{
            String channelKey = ChannelUtil.getChannelKey(ctx.channel());
            Msg request = (Msg) msg;
            ActorRef actorRef = ActorManager.Instance.getConnectActor(channelKey);
            if(actorRef == null){
                log.error("channelRead actorRef is null! channelKey:{}", channelKey);
                return;
            }

            actorRef.tell(request, ActorRef.noSender());
        }catch (Exception e){
            log.error("channelRead error",e);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String channelKey = ChannelUtil.getChannelKey(ctx.channel());
        ChannelCache.CONTEXT_CACHE.remove(channelKey);
        String openId = ChannelCache.CHANNEL_TO_USER_CACHE.remove(channelKey);
        if(StringUtils.isEmpty(openId)){
            ChannelCache.USER_TO_CHANNEL_CACHE.remove(openId);
        }
        ActorManager.Instance.stopActor(channelKey);
        ChannelRouterRedisDao.Instance.removeRouterAddress(channelKey);
        super.channelInactive(ctx);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx)
            throws Exception {
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
            throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            switch (event.state()) {
                case ALL_IDLE:
                    log.info("{}ALL_IDLE:{}", ctx, ctx.channel());
                    break;
                case READER_IDLE:
                    log.info("{}READER_IDLE:{}", ctx, ctx.toString());
                    ctx.close();
                    break;
                case WRITER_IDLE:
                    log.info("{}WRITER_IDLE:{}", ctx, ctx.toString());
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
    }
}
