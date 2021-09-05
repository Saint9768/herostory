package org.tinygame.herostory;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * 解码器
 *
 * @author 周鑫(玖枭)
 */
public class GameMsgEncoder extends ChannelOutboundHandlerAdapter {

    static final Logger LOGGER = LoggerFactory.getLogger(GameMsgDecoder.class);

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (Objects.isNull(msg) || Objects.isNull(ctx)) {
            return;
        }

        try {
            // 不是Protobuf消息，直接使用父类的write()方法
            if (!(msg instanceof GeneratedMessageV3)) {
                super.write(ctx, msg, promise);
                return;
            }

            // 消息編碼
            int msgCode = GameMsgRecognizer.getMsgCodeByClazz(msg.getClass());

            if (-1 == msgCode) {
                LOGGER.error("无法识别的消息类型，msgClazz = {}", msg.getClass().getSimpleName());
                super.write(ctx, msg, promise);
                return;
            }

            byte[] msgBody = ((GeneratedMessageV3) msg).toByteArray();
            ByteBuf byteBuf = ctx.alloc().buffer();
            // 消息长度
            byteBuf.writeShort((short) msgBody.length);
            // 消息编号
            byteBuf.writeShort((short) msgCode);
            // 消息体
            byteBuf.writeBytes(msgBody);
            BinaryWebSocketFrame outputFrame = new BinaryWebSocketFrame(byteBuf);

            super.write(ctx, outputFrame, promise);

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    public void flush(ChannelHandlerContext ctx) throws Exception {
        super.flush(ctx);
    }
}
