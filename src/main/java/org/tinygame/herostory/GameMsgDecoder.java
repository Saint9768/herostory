package org.tinygame.herostory;

import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 自定义消息解码器
 *
 * @author 周鑫(玖枭)
 */
public class GameMsgDecoder extends ChannelInboundHandlerAdapter {

    static final Logger LOGGER = LoggerFactory.getLogger(GameMsgDecoder.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (null == ctx || null == msg) {
            return;
        }

        if (!(msg instanceof BinaryWebSocketFrame)) {
            return;
        }
        try {

            // 将消息强转为 网络套接字包含二进制数据帧
            BinaryWebSocketFrame inputFrame = (BinaryWebSocketFrame) msg;
            // 获取网络套接字的字节缓冲区
            ByteBuf content = inputFrame.content();

            // 读取消息的长度， 两字节数据
            content.readShort();

            // 读取消息编号， 两字节数据
            int msgCode = content.readShort();

            // 读取消息体
            byte[] msgBody = new byte[content.readableBytes()];
            content.readBytes(msgBody);

            // google的消息体
            Message.Builder msgBuilder = GameMsgRecognizer.getBuilderByMsgCode(msgCode);
            if (null == msgBuilder) {
                return;
            }
            msgBuilder.clear();
            msgBuilder.mergeFrom(msgBody);
            Message cmd = msgBuilder.build();

            if (null != cmd) {
                ctx.fireChannelRead(cmd);
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }
}
