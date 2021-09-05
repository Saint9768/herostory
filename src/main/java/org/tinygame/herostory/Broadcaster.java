package org.tinygame.herostory;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * 广播员
 *
 * @author Saint
 * @version 1.0
 * @createTime 2021-08-25 6:11
 */
public final class Broadcaster {

    /**
     * 信道组，注意这里一定要用static
     * 否者无法实现群发
     */
    static private final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public Broadcaster() {

    }

    /**
     * 添加信道
     *
     * @param ch
     */
    static public void addChannel(Channel ch) {
        if (null != ch) {
            channelGroup.add(ch);
        }
    }

    /**
     * 移出信道
     *
     * @param ch
     */
    static public void removeChannel(Channel ch) {
        if (null != ch) {
            channelGroup.remove(ch);
        }
    }

    /**
     * 广播消息
     */
    static public void broadcast(Object msg) {
        if (null != msg) {
            channelGroup.writeAndFlush(msg);
        }
    }



}
