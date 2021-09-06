package org.tinygame.herostory.async;

/**
 * 异步操作接口
 *
 * @author Saint
 * @version 1.0
 * @createTime 2021-09-06 7:17
 */
public interface IAsyncOperation {

    /**
     * 获取绑定 Id
     *
     * @return 绑定 Id
     */
    default int getBindId() {
        return 0;
    }

    /**
     * 执行异步操作
     */
    void doAsync();

    /**
     * 执行完成逻辑
     */
    default void doFinish() {
    }
}
