package org.tinygame.herostory.cmdhandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.tinygame.herostory.Broadcaster;
import org.tinygame.herostory.model.User;
import org.tinygame.herostory.model.UserManager;
import org.tinygame.herostory.msg.GameMsgProtocol;

import java.util.Objects;

/**
 * 用户移动处理类
 *
 * @author Saint
 * @version 1.0
 * @createTime 2021-08-25 6:35
 */
public class UserMoveToCmdHandler implements ICmdHandler<GameMsgProtocol.UserMoveToCmd> {

    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserMoveToCmd cmd) {

        if (null == cmd || null == ctx) {
            return;
        }

        // 从contextChannel中获取用户ID
        Integer userId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();
        if (Objects.isNull(userId)) {
            return;
        }

        // 获取已有用户
        User existUser = UserManager.getByUserId(userId);
        if(null == existUser) {
            return;
        }

        long nowTime = System.currentTimeMillis();

        existUser.moveState.fromPosX = cmd.getMoveFromPosX();
        existUser.moveState.fromPoxy = cmd.getMoveFromPosY();
        existUser.moveState.toPosX = cmd.getMoveToPosX();
        existUser.moveState.toPoxy = cmd.getMoveToPosY();
        existUser.moveState.startTime = nowTime;

        GameMsgProtocol.UserMoveToResult.Builder resultBuilder = GameMsgProtocol.UserMoveToResult.newBuilder();
        // 当前用户ID
        resultBuilder.setMoveUserId(userId);
        // 开始移动位置
        resultBuilder.setMoveFromPosX(cmd.getMoveFromPosX());
        resultBuilder.setMoveFromPosY(cmd.getMoveFromPosY());
        // 目标移动位置
        resultBuilder.setMoveToPosX(cmd.getMoveToPosX());
        resultBuilder.setMoveToPosY(cmd.getMoveToPosY());
        // 开始移动时间
        resultBuilder.setMoveStartTime(nowTime);
        GameMsgProtocol.UserMoveToResult newResult = resultBuilder.build();
        // 群发消息
        Broadcaster.broadcast(newResult);
    }
}
