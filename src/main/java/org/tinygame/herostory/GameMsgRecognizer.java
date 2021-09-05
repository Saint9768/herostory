package org.tinygame.herostory;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.msg.GameMsgProtocol;

import java.util.HashMap;
import java.util.Map;

/**
 * 消息识别器
 *
 * @author Saint
 * @version 1.0
 * @createTime 2021-08-25 7:02
 */
public final class GameMsgRecognizer {

    static Logger LOGGER = LoggerFactory.getLogger(GameMsgRecognizer.class);

    private GameMsgRecognizer() {

    }

    /**
     * 消息编号 --> 消息对象字典
     */
    static private final Map<Integer, GeneratedMessageV3> msgCodeAndMsgObjectMap = new HashMap<>();

    /**
     * 消息类 --> 消息编号字典
     */
    static private final Map<Class<?>, Integer> msgClazzAndMsgCodeMap = new HashMap<>();

    public static void init() {
        LOGGER.info("=====   开始消息类与消息编号的映射    =========");

        // 使用反射
        // 获取内部类
        Class<?>[] innerClazzArray = GameMsgProtocol.class.getDeclaredClasses();
        if (null == innerClazzArray) {
            return;
        }
        for (Class<?> innerClazz : innerClazzArray) {
            // 如果不是消息类跳过
            if (null == innerClazz ||
                    !GeneratedMessageV3.class.isAssignableFrom(innerClazz)) {
                continue;
            }

            // 拿到类名(小写)
            String clazzName = innerClazz.getSimpleName();
            clazzName = clazzName.toLowerCase();
            // 遍历枚举值
            for (GameMsgProtocol.MsgCode msgCode : GameMsgProtocol.MsgCode.values()) {
                if (null == msgCode) {
                    continue;
                }

                // 拿到枚举值的名称 消息编码
                String strMsgCode = msgCode.name();
                strMsgCode = strMsgCode.replaceAll("_", "").toLowerCase();

                if (!strMsgCode.startsWith(clazzName)) {
                    continue;
                }

                try {
                    // 相当于调用 UserEntryCmd.getDefaultInstance();
                    Object returnObj = innerClazz.getDeclaredMethod("getDefaultInstance").invoke(innerClazz);

                    LOGGER.info("{} <==> {}", innerClazz.getName(), msgCode.getNumber());
                    msgCodeAndMsgObjectMap.putIfAbsent(
                            msgCode.getNumber(),
                            (GeneratedMessageV3) returnObj
                    );

                    msgClazzAndMsgCodeMap.putIfAbsent(
                            innerClazz,
                            msgCode.getNumber()
                    );
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }

            }
        }

        LOGGER.info("=====   完成消息类与消息编号的映射    =========");

//        msgCodeAndMsgObjectMap.putIfAbsent(GameMsgProtocol.MsgCode.USER_ENTRY_CMD_VALUE,
//                GameMsgProtocol.UserEntryCmd.getDefaultInstance());
//        msgCodeAndMsgObjectMap.putIfAbsent(GameMsgProtocol.MsgCode.WHO_ELSE_IS_HERE_CMD_VALUE,
//                GameMsgProtocol.WhoElseIsHereCmd.getDefaultInstance());
//        msgCodeAndMsgObjectMap.putIfAbsent(GameMsgProtocol.MsgCode.USER_MOVE_TO_CMD_VALUE,
//                GameMsgProtocol.UserMoveToCmd.getDefaultInstance());
//
//        msgClazzAndMsgCodeMap.putIfAbsent(GameMsgProtocol.UserEntryResult.class,
//                GameMsgProtocol.MsgCode.USER_ENTRY_RESULT_VALUE);
//        msgClazzAndMsgCodeMap.putIfAbsent(GameMsgProtocol.WhoElseIsHereResult.class,
//                GameMsgProtocol.MsgCode.WHO_ELSE_IS_HERE_RESULT_VALUE);
//        msgClazzAndMsgCodeMap.putIfAbsent(GameMsgProtocol.UserMoveToResult.class,
//                GameMsgProtocol.MsgCode.USER_MOVE_TO_RESULT_VALUE);
//        msgClazzAndMsgCodeMap.putIfAbsent(GameMsgProtocol.UserQuitResult.class,
//                GameMsgProtocol.MsgCode.USER_QUIT_RESULT_VALUE);
    }

    /**
     * 解码时根据消息编码获取到到一个空的命令实例
     * @param msgCode
     * @return
     */
    public static Message.Builder getBuilderByMsgCode(int msgCode) {
        if (msgCode < 0) {
            return null;
        }
        GeneratedMessageV3 defaultMsg = msgCodeAndMsgObjectMap.get(msgCode);

        if (null == defaultMsg) {
            return null;
        }
        return defaultMsg.newBuilderForType();
    }

    /**
     * 编码时，根据命令类获取到消息编码
     * @param msgClazz
     * @return
     */
    public static Integer getMsgCodeByClazz(Class<?> msgClazz) {
        if (null == msgClazz) {
            return -1;
        }
        Integer msgCode = msgClazzAndMsgCodeMap.get(msgClazz);
        if (null == msgCode) {
            return -1;
        }
        return msgCode;
    }
}
