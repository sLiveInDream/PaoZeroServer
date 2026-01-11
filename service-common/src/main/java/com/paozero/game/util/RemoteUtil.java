package com.paozero.game.util;

import com.paozero.game.protobuf.RpcRequest;
import org.apache.dubbo.rpc.Invocation;

public class RemoteUtil {
    public static String getChannelKeyFromInvocation(Invocation invocation) {
        Object[] args = invocation.getArguments();
        if (args == null || args.length != 1) {
            return null;
        }

        if (invocation.getParameterTypes()[0] != RpcRequest.class) {
            return null;
        }

        RpcRequest request = (RpcRequest) args[0];
        return request.getChannelKey();
    }
}
