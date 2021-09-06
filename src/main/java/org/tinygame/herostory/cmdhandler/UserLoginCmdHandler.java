package org.tinygame.herostory.cmdhandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static Logger LOGGER = LoggerFactory.getLogger(UserLoginCmdHandler.class);

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

        LOGGER.info("当前线程 = {}", Thread.currentThread().getName());

        // 获取数据库中的用户实体, 回调函数
        LoginService.getInstance().userLogin(userName, password, userEntity -> {
            GameMsgProtocol.UserLoginResult.Builder resultBuilder = GameMsgProtocol.UserLoginResult.newBuilder();
            LOGGER.info("当前线程 = {}", Thread.currentThread().getName());
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
            return null;
        });


    }
}
