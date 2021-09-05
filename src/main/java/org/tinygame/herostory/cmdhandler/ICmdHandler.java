package org.tinygame.herostory.cmdhandler;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.channel.ChannelHandlerContext;

/**
 * 抽象接口
 * @author Saint
 * @version 1.0
 * @createTime 2021-08-25 6:38
 */
public interface ICmdHandler<TCmd extends GeneratedMessageV3> {

    /**
     * 处理核心
     * @param ctx
     * @param cmd
     */
    void handle (ChannelHandlerContext ctx, TCmd cmd);
}
