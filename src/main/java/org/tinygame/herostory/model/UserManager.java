package org.tinygame.herostory.model;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用户管理器
 *
 * @author Saint
 * @version 1.0
 * @createTime 2021-08-25 6:18
 */
public final class UserManager {

    /**
     * 记录用户列表
     */
    static private final Map<Integer, User> userMap = new ConcurrentHashMap<>();

    private UserManager() {

    }

    /**
     * 添加用户
     *
     * @param user
     */
    public static void addUser(User user) {
        if (null != user) {
            userMap.putIfAbsent(user.getUserId(), user);
        }
    }

    /**
     * 移出用户
     *
     * @param userId
     */
    public static void removeByUserId(Integer userId) {
        if (null != userId) {
            userMap.remove(userId);
        }
    }

    /**
     * 根据用户ID获取用户
     *
     * @param userId
     * @return
     */
    public static User getByUserId(Integer userId) {
        return userMap.get(userId);
    }

    /**
     * 返回用户列表
     *
     * @return
     */
    public static Collection<User> listUser() {
        return userMap.values();
    }


}
