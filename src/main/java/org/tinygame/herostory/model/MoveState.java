package org.tinygame.herostory.model;

/**
 * 移动状态
 * @author Saint
 * @version 1.0
 * @createTime 2021-08-30 7:40
 */
public class MoveState {

    /**
     * 起始位置 -X轴
     */
    public float fromPosX;

    /**
     * 起始位置 -Y轴
     */
    public float fromPoxy;

    /**
     * 目标位置 -X轴
     */
    public float toPosX;

    /**
     * 目标位置 -Y轴
     */
    public float toPoxy;

    /**
     * 开始移动时间
     */
    public long startTime;
}
