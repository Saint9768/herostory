package org.tinygame.herostory.cmdhandler;

import com.google.protobuf.GeneratedMessageV3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.util.PackageUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 命令处理器工厂类（简单工厂）
 * 但是处理器类应该是单例的
 *
 * @author Saint
 * @version 1.0
 * @createTime 2021-08-25 6:44
 */
public class CmdHandlerFactory {

    static final private Logger LOGGER = LoggerFactory.getLogger(CmdHandlerFactory.class);

    /**
     * 命令处理器字典
     */
    static private final Map<Class<?>, ICmdHandler<? extends GeneratedMessageV3>> handlerMap = new HashMap<>();

    private CmdHandlerFactory() {

    }

    /**
     * 初始化命令
     */
    static public void init() {

        LOGGER.info("=====    开始命令与处理器的关联      =====");

        // 获取包名称
        final String packageName = CmdHandlerFactory.class.getPackage().getName();

        // 获取 ICmdHandler 所有的实现类
        Set<Class<?>> clazzSet = PackageUtil.listSubClazz(
                packageName,
                true,
                ICmdHandler.class);

        for (Class<?> clazz : clazzSet) {
            if (null == clazz ||
                    0 != (clazz.getModifiers() & Modifier.ABSTRACT)) {
                continue;
            }

            // 获取方法数组
            Method[] methodArray = clazz.getDeclaredMethods();

            // 可用的消息类型
            Class<?> msgClazz = null;

            for (Method currMethod : methodArray) {
                // 如果不是handle()方法跳过
                if (null == currMethod || !currMethod.getName().equals("handle")) {
                    continue;
                }

                // 获取函数参数类型数组
                Class<?>[] parameterTypes = currMethod.getParameterTypes();

                // 参数个数必须大于等于2，并且第二个参数类型是GeneratedMessageV3的子类
                if (parameterTypes.length < 2 ||
                        parameterTypes[1] == GeneratedMessageV3.class ||
                        !GeneratedMessageV3.class.isAssignableFrom(parameterTypes[1])) {
                    continue;
                }

                // 拿到消息类型 xxxCmd
                msgClazz = parameterTypes[1];
                break;
            }

            if (null == msgClazz) {
                continue;
            }

            try {
                // 创建命令处理器实例
                ICmdHandler<?> newHandler = (ICmdHandler<?>) clazz.newInstance();

                LOGGER.info(
                        "{} <====> {}",
                        msgClazz.getName(),
                        clazz.getName()
                );
                handlerMap.putIfAbsent(msgClazz, newHandler);
            } catch (Exception e) {

            }
        }

        LOGGER.info("=====    完成命令与处理器的关联      =====");

//        handlerMap.putIfAbsent(GameMsgProtocol.UserEntryCmd.class, new UserEntryCmdHandler());
//        handlerMap.putIfAbsent(GameMsgProtocol.WhoElseIsHereCmd.class, new WhoElseIsHereCmdHandler());
//        handlerMap.putIfAbsent(GameMsgProtocol.UserMoveToCmd.class, new UserMoveToCmdHandler());
    }

    /**
     * 创建命令处理器
     *
     * @param msgClazz
     * @return
     */
    static public ICmdHandler<? extends GeneratedMessageV3> create(Class<?> msgClazz) {
        if (null == msgClazz) {
            return null;
        }
        return handlerMap.get(msgClazz);
    }


}
