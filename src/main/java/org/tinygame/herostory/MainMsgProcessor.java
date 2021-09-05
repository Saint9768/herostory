package org.tinygame.herostory;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.cmdhandler.CmdHandlerFactory;
import org.tinygame.herostory.cmdhandler.ICmdHandler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 主消息处理器
 *
 * @author Saint
 * @version 1.0
 * @createTime 2021-09-02 7:20
 */
public class MainMsgProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainMsgProcessor.class);

    /**
     * 单例对象
     */
    private static final MainMsgProcessor instance = new MainMsgProcessor();

    private MainMsgProcessor() {

    }

    /**
     * 创建一个单线程的线程池
     */
    private final ExecutorService es = Executors.newSingleThreadExecutor((runnable) -> {
       Thread newThread = new Thread(runnable);
       newThread.setName("MainMsgProcessor");
       return newThread;
    });

    public static MainMsgProcessor getInstance() {
        return instance;
    }

    public void process(ChannelHandlerContext ctx, Object msg) {

        LOGGER.info("收到客户端消息, msgClazz = {}, msg = {}", msg.getClass().getSimpleName(), msg);

        // 保证处理命令的逻辑放在单线程中执行。
        es.submit(() -> {
            try {
                // 获取命令处理器
                ICmdHandler<? extends GeneratedMessageV3> cmdHandler = CmdHandlerFactory.create(msg.getClass());

                if (null != cmdHandler) {
                    cmdHandler.handle(ctx, cast(msg));
                }

            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        });
    }

    /**
     * 强转为命令对象
     *
     * @return
     */
    static private <TCmd extends GeneratedMessageV3> TCmd cast(Object msg) {
        if (null == msg) {
            return null;
        }
        return (TCmd) msg;
    }
}
