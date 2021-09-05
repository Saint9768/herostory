package org.tinygame.herostory.cmdhandler;

import io.netty.channel.ChannelHandlerContext;
import org.tinygame.herostory.model.User;
import org.tinygame.herostory.model.UserManager;
import org.tinygame.herostory.msg.GameMsgProtocol;

import java.util.Collection;
import java.util.Objects;


/**
 * 有谁在场，需要指定发送消息给他
 *
 * @author Saint
 * @version 1.0
 * @createTime 2021-08-25 6:31
 */
public class WhoElseIsHereCmdHandler implements ICmdHandler<GameMsgProtocol.WhoElseIsHereCmd> {

    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.WhoElseIsHereCmd cmd) {
        if (null == cmd || null == ctx) {
            return;
        }

        // 有谁在场，需要指定发送消息给他
        GameMsgProtocol.WhoElseIsHereResult.Builder resultBuilder = GameMsgProtocol.WhoElseIsHereResult.newBuilder();

        // 获取用户列表
        Collection<User> users = UserManager.listUser();
        for (User currUser : users) {
            if (Objects.isNull(currUser)) {
                continue;
            }

            GameMsgProtocol.WhoElseIsHereResult.UserInfo.Builder userInfoBuilder = GameMsgProtocol.WhoElseIsHereResult.UserInfo.newBuilder();
            userInfoBuilder.setUserId(currUser.getUserId());
            userInfoBuilder.setHeroAvatar(currUser.getHeroAvatar());

            // 构建移动状态
            GameMsgProtocol.WhoElseIsHereResult.UserInfo.MoveState.Builder mvStateBuilder =
                    GameMsgProtocol.WhoElseIsHereResult.UserInfo.MoveState.newBuilder();
            mvStateBuilder.setFromPosX(currUser.moveState.fromPosX);
            mvStateBuilder.setFromPosY(currUser.moveState.fromPoxy);
            mvStateBuilder.setToPosX(currUser.moveState.toPosX);
            mvStateBuilder.setToPosY(currUser.moveState.toPoxy);
            mvStateBuilder.setStartTime(currUser.moveState.startTime);
            userInfoBuilder.setMoveState(mvStateBuilder);

            resultBuilder.addUserInfo(userInfoBuilder);
        }
        GameMsgProtocol.WhoElseIsHereResult newResult = resultBuilder.build();
        ctx.writeAndFlush(newResult);
    }
}
