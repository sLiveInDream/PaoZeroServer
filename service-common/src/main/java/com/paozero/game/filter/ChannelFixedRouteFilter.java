package com.paozero.game.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.RpcException;

@Slf4j
@Activate(group = {CommonConstants.PROVIDER})
public class ChannelFixedRouteFilter implements Filter {
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        String gateway_serverId = RpcContext.getServerContext().getAttachment("gateway_serverId");
        log.info("gateway_serverId:{}", gateway_serverId);
        return invoker.invoke(invocation);
    }
}
