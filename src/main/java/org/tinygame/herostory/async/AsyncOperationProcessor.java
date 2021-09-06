package org.tinygame.herostory.async;

import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.MainMsgProcessor;

import java.text.MessageFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 异步操作处理器
 *
 * @author Saint
 * @version 1.0
 * @createTime 2021-09-06 6:50
 */
public class AsyncOperationProcessor {

    Logger LOGGER = LoggerFactory.getLogger(AsyncOperationProcessor.class);

    /**
     * 单例对象
     */
    private static  final AsyncOperationProcessor instance = new AsyncOperationProcessor();

    /**
     * 单线程组
     */
    private final ExecutorService[] esArray = new ExecutorService[8];

    private AsyncOperationProcessor() {
        for (int i = 0; i < esArray.length; i ++) {
            final String threadName = MessageFormat.format("AsyncOperationProcessor[ {0} ]", i);
            // 创建单线程
            esArray[i] = Executors.newSingleThreadExecutor((runnable) -> {
                Thread newThread = new Thread(runnable);
                newThread.setName(threadName);
                return newThread;
            });
        }
    }

    public static AsyncOperationProcessor getInstance() {
        return instance;
    }

    public void process(IAsyncOperation op) {
        if (null == op) {
            return;
        }

        // 每个用户过来之后它所有的请求都会进入到一个线程中去执行。
        int bindId = Math.abs(op.getBindId());
        int esIndex = bindId % esArray.length;

        esArray[esIndex].submit(() -> {
            // 执行异步操作
            op.doAsync();
            // 执行完成逻辑，并将其返回到主线程中去执行
            MainMsgProcessor.getInstance().process(op::doFinish);
        });
    }


}
