package org.tinygame.herostory.login;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.MySqlSessionFactory;
import org.tinygame.herostory.async.AsyncOperationProcessor;
import org.tinygame.herostory.async.IAsyncOperation;
import org.tinygame.herostory.login.db.IUserDao;
import org.tinygame.herostory.login.db.UserEntity;

import java.util.function.Function;

/**
 * 登陆服务
 */
public final class LoginService {
    /**
     * 日志服务
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(LoginService.class);

    /**
     * 单例对象
     */
    static private final LoginService _instance = new LoginService();

    /**
     * 私有化类默认构造器
     */
    private LoginService() {
    }

    /**
     * 获取单例对象
     *
     * @return
     */
    static public LoginService getInstance() {
        return _instance;
    }

    /**
     * 用户登陆
     *
     * @param userName
     * @param password
     * @param callback 回调函数
     * @return
     */
    public void userLogin(String userName, String password, Function<UserEntity, Void> callback) {
        if (null == userName ||
            null == password) {
            return ;
        }
        AsyncOperationProcessor.getInstance().process( new AsyncGetUserEntity(userName, password) {
            @Override
            public int getBindId() {
                return (userName + password).hashCode();
            }

            @Override
            public void doFinish() {
                if (null != callback) {
                    // 把UserEntity给传回去，传到主线程
                    callback.apply(this.getUserEntity());
                }
            }
        });
    }

    private class AsyncGetUserEntity implements IAsyncOperation {

        /**
         * 用户名称
         */
        private final String userName;

        /**
         * 密码
         */
        private final String password;

        /**
         * 用户实体
         */
        private UserEntity userEntity;

        /**
         * 类参数构造器
         *
         * @param userName 用户名称
         * @param password 密码
         */
        AsyncGetUserEntity(String userName, String password) {
            this.userName = userName;
            this.password = password;
        }

        /**
         * 获取用户实体
         *
         * @return 用户实体
         */
        UserEntity getUserEntity() {
            return userEntity;
        }

        @Override
        public int getBindId() {
            if (null == userName) {
                return 0;
            } else {
                return userName.charAt(userName.length() - 1);
            }
        }

        @Override
        public void doAsync() {
            try (SqlSession mySqlSession = MySqlSessionFactory.openSession()) {
                // 获取 DAO
                IUserDao dao = mySqlSession.getMapper(IUserDao.class);
                // 获取用户实体
                UserEntity userEntity = dao.getByUserName(userName);

                LOGGER.info("当前线程 = {}", Thread.currentThread().getName());

                if (null != userEntity) {
                    if (!password.equals(userEntity.password)) {
                        throw new RuntimeException("密码错误");
                    }
                } else {
                    userEntity = new UserEntity();
                    userEntity.userName = userName;
                    userEntity.password = password;
                    userEntity.heroAvatar = "Hero_Shaman";

                    dao.insertInto(userEntity);
                }
                this.userEntity = userEntity;
            } catch (Exception ex) {
                // 记录错误日志
                LOGGER.error(ex.getMessage(), ex);
            }
        }
    }
}
