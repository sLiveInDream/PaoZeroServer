package com.paozero.game.router;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.cluster.router.state.StateRouter;
import org.apache.dubbo.rpc.cluster.router.state.StateRouterFactory;

public class ChannelFixedStateRouterFactory implements StateRouterFactory {
    @Override
    public <T> StateRouter<T> getRouter(Class<T> interfaceClass, URL url) {
        return new ChannelFixedStateRouter<>(url);
    }
}
