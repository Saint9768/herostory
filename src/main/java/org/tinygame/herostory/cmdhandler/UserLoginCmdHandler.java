package org.tinygame.herostory.cmdhandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.tinygame.herostory.login.LoginService;
import org.tinygame.herostory.login.db.UserEntity;
import org.tinygame.herostory.model.User;
import org.tinygame.herostory.model.UserManager;
import org.tinygame.herostory.msg.GameMsgProtocol;

/**
 * 用户登录
 *
 * @author Saint
 * @version 1.0
 * @createTime 2021-09-02 7:57
 */
public class UserLoginCmdHandler implements ICmdHandler<GameMsgProtocol.UserLoginCmd> {
    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserLoginCmd cmd) {
        if (null == ctx ||
        null == cmd) {
            return;
        }

        String userName = cmd.getUserName();
        String password = cmd.getPassword();

        if (null == userName || null == password) {
            return;
        }

        // 获取数据库中的用户实体
        UserEntity userEntity = LoginService.getInstance().userLogin(userName, password);

        GameMsgProtocol.UserLoginResult.Builder resultBuilder = GameMsgProtocol.UserLoginResult.newBuilder();

        if (null == userEntity) {
            resultBuilder.setUserId(-1);
            resultBuilder.setUserName("");
            resultBuilder.setHeroAvatar("");
        } else {
            User newUser = new User();
            newUser.setUserId(userEntity.userId);
            newUser.userName = userEntity.userName;
            newUser.setHeroAvatar(userEntity.heroAvatar);
            newUser.currHp = 100;
            UserManager.addUser(newUser);

            // 用户入场的时候，把自己的ID放到自己关联的信道里
            // 即将用户ID保存到Session中
            ctx.channel().attr(AttributeKey.valueOf("userId")).set(newUser.userId);
            resultBuilder.setUserId(userEntity.userId);
            resultBuilder.setUserName(userEntity.userName);
            resultBuilder.setHeroAvatar(userEntity.heroAvatar);
        }

        GameMsgProtocol.UserLoginResult userLoginResult = resultBuilder.build();
        ctx.writeAndFlush(userLoginResult);

    }
}
