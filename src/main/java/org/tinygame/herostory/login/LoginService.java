package org.tinygame.herostory.login;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.MySqlSessionFactory;
import org.tinygame.herostory.login.db.IUserDao;
import org.tinygame.herostory.login.db.UserEntity;

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
     * @return
     */
    public UserEntity userLogin(String userName, String password) {
        if (null == userName ||
            null == password) {
            return null;
        }

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

            return userEntity;
        } catch (Exception ex) {
            // 记录错误日志
            LOGGER.error(ex.getMessage(), ex);
            return null;
        }
    }
}
