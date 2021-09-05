package org.tinygame.herostory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.model.UserManager;
import org.tinygame.herostory.msg.GameMsgProtocol;

import java.util.Objects;

/**
 * 自定义的消息处理器
 */
public class GameMsgHandler extends SimpleChannelInboundHandler<Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameMsgHandler.class);

    /**
     * 服务下线逻辑
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {

        if (null == ctx) {
            return;
        }

        try {

            // 这个一定要放在最上面，否者会在屏幕最中间留下一个"人物影子"
            super.handlerRemoved(ctx);

            // 从contextChannel中获取用户ID
            Integer userId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();
            if (Objects.isNull(userId)) {
                return;
            }

            // 用户列表中移除用户
            UserManager.removeByUserId(userId);
            Broadcaster.removeChannel(ctx.channel());

            // 退场消息
            GameMsgProtocol.UserQuitResult.Builder resultBuilder = GameMsgProtocol.UserQuitResult.newBuilder();
            resultBuilder.setQuitUserId(userId);

            GameMsgProtocol.UserQuitResult newResult = resultBuilder.build();
            // 群发广播
            Broadcaster.broadcast(newResult);

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * 服务连接上时
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        if (Objects.isNull(ctx)) {
            return;
        }

        try {
            super.channelActive(ctx);
            // 将所有存活的信道放到信道组里，否者无法通知其他客户端
            Broadcaster.addChannel(ctx.channel());
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (Objects.equals(null, ctx) || Objects.equals(null, msg)) {
            return;
        }

        MainMsgProcessor.getInstance().process(ctx, msg);
    }

}
