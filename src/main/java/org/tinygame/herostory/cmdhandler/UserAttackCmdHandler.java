package org.tinygame.herostory.cmdhandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.Broadcaster;
import org.tinygame.herostory.model.User;
import org.tinygame.herostory.model.UserManager;
import org.tinygame.herostory.msg.GameMsgProtocol;

/**
 * 用户攻击命令处理器
 *
 * @author Saint
 * @version 1.0
 * @createTime 2021-08-26 7:12
 */
public class UserAttackCmdHandler implements ICmdHandler<GameMsgProtocol.UserAttkCmd> {

    static private final Logger LOGGER = LoggerFactory.getLogger(UserAttackCmdHandler.class);

    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserAttkCmd cmd) {
        LOGGER.info("用户攻击");

        if (null == ctx || null == cmd) {
            return;
        }

        // 获取目标用户 ID
        Integer attkUserId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();
        if (null == attkUserId) {
            return;
        }

        // 获取目标用户
        int targetUserId = cmd.getTargetUserId();
        User targetUser = UserManager.getByUserId(targetUserId);
        if (null == targetUser) {
            // 没攻击到人也要广播消息
            broadcastAttkResult(attkUserId, -1);
            return;
        }

        LOGGER.info("当前线程 = {}", Thread.currentThread().getName());

        final int dmgPoint = 10;
        targetUser.currHp = targetUser.currHp - dmgPoint;

        // 广播攻击结果
        broadcastAttkResult(attkUserId, targetUserId);
        // 广播扣血结果
        broadcastSubtractHpResult(targetUserId, dmgPoint);

        if (targetUser.currHp <= 0) {
            // 广播死亡结果
            broadcastDieResult(targetUserId);
        }

    }

    /**
     * 广播攻击结果
     *
     * @param attkUserId   攻击人
     * @param targetUserId 受害人
     */
    private static void broadcastAttkResult(int attkUserId, int targetUserId) {
        if (attkUserId <= 0) {
            return;
        }

        GameMsgProtocol.UserAttkResult.Builder resultBuilder = GameMsgProtocol.UserAttkResult.newBuilder();
        resultBuilder.setAttkUserId(targetUserId);
        resultBuilder.setAttkUserId(attkUserId);
        GameMsgProtocol.UserAttkResult newResult = resultBuilder.build();
        Broadcaster.broadcast(newResult);

    }

    /**
     * 减血操作
     *
     * @param targetUserId
     * @param subtractHp
     */
    private static void broadcastSubtractHpResult(int targetUserId, int subtractHp) {
        if (targetUserId <= 0 || subtractHp <= 0) {
            return;
        }

        GameMsgProtocol.UserSubtractHpResult.Builder resultBuilder = GameMsgProtocol.UserSubtractHpResult.newBuilder();
        resultBuilder.setTargetUserId(targetUserId);
        resultBuilder.setSubtractHp(subtractHp);
        GameMsgProtocol.UserSubtractHpResult newResult = resultBuilder.buildPartial();
        Broadcaster.broadcast(newResult);
    }

    /**
     * 广播死亡结果
     *
     * @param targetUserId
     */
    private static void broadcastDieResult(int targetUserId) {
        if (targetUserId <= 0) {
            return;
        }

        GameMsgProtocol.UserDieResult.Builder resultBuilder = GameMsgProtocol.UserDieResult.newBuilder();
        resultBuilder.setTargetUserId(targetUserId);
        GameMsgProtocol.UserDieResult newResult = resultBuilder.build();
        Broadcaster.broadcast(newResult);
    }
}
