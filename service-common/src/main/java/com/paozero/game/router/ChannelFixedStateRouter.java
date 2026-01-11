package com.paozero.game.router;

import com.paozero.game.protobuf.NotifyService;
import com.paozero.game.protobuf.RpcRequest;
import com.paozero.game.redis.dao.ChannelRouterRedisDao;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.utils.Holder;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.cluster.router.RouterSnapshotNode;
import org.apache.dubbo.rpc.cluster.router.state.AbstractStateRouter;
import org.apache.dubbo.rpc.cluster.router.state.BitList;

import java.util.Random;

@Slf4j
public class ChannelFixedStateRouter<T> extends AbstractStateRouter<T> {
    private final Random random = new Random();

    public ChannelFixedStateRouter(URL url) {
        super(url);
    }

    @Override
    protected BitList<Invoker<T>> doRoute(BitList<Invoker<T>> bitList, URL url, Invocation invocation, boolean needToPrintMessage, Holder<RouterSnapshotNode<T>> holder, Holder<String> messageHolder) throws RpcException {
        Object[] args = invocation.getArguments();
        if (args == null || args.length != 1) {
            return null;
        }

        if (invocation.getParameterTypes()[0] != RpcRequest.class) {
            return null;
        }

        String serviceName = invocation.getServiceName();
        RpcRequest request = (RpcRequest) args[0];
        String channelKey = request.getChannelKey();

        String invokerAddress = ChannelRouterRedisDao.Instance.getRouterAddress(channelKey, serviceName);
        if (StringUtils.isEmpty(invokerAddress)) {
            if (serviceName.equals(NotifyService.class.getName())) {
                log.error("router select NotifyService address is null! channelKey is disConnected or NotifyService address is not registered! channelKey:{}", channelKey);
                return null;
            }
            invokerAddress = ChannelRouterRedisDao.Instance.putRouterAddressIfAbsent(channelKey, serviceName, loadBalanceInvoker(serviceName, bitList));
        }

        log.info("router serviceName:{}, invokerAddress:{}", serviceName, invokerAddress);
        BitList<Invoker<T>> targetList = new BitList<>(BitList.emptyList());
        for (Invoker<T> invoker : bitList) {
            if (invoker.getUrl().getAddress().equals(invokerAddress)) {
                targetList.add(invoker);
            }
        }

        //如果路由目标已下线，则重新路由一次
        if (targetList.isEmpty()) {
            if (serviceName.equals(NotifyService.class.getName())) {
                log.error("router select NotifyService address is null! channelKey is disConnected or gateway is offline! channelKey:{}, gateway address:{}", channelKey, invokerAddress);
                return null;
            }
            String newAddress = loadBalanceInvoker(serviceName, bitList);
            ChannelRouterRedisDao.Instance.putRouterAddress(channelKey, serviceName, newAddress);

            log.info("router serviceName:{}, invokerAddress:{} is offline, reroute to newAddress:{}", serviceName, invokerAddress, newAddress);
            for (Invoker<T> invoker : bitList) {
                if (invoker.getUrl().getAddress().equals(newAddress)) {
                    targetList.add(invoker);
                }
            }
        }

        //如果最终无法找到有效的provider
        if (targetList.isEmpty()) {
            log.error("router error! can not find valid provider! channelKey:{}, serviceName:{}", channelKey, serviceName);
        }

        return targetList;
    }

    private <T> String loadBalanceInvoker(String serviceName, BitList<Invoker<T>> bitList) {
        //todo 先随机选一个invoker,未来可扩展为选人最少的
        if (bitList == null || bitList.isEmpty()) {
            return "";
        }
        int index = random.nextInt(bitList.size());
        String address = bitList.get(index).getUrl().getAddress();
        log.info("loadBalanceInvoker serviceName:{}, address:{}", serviceName, address);

        return address;
    }
}
