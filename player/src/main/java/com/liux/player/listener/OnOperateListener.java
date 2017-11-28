package com.liux.player.listener;

/**
 * Created by Liux on 2017/11/26.
 */

public interface OnOperateListener {

    /**
     * 调用开始
     */
    void callStart();

    /**
     * 调用暂停
     */
    void callPause();

    /**
     * 调用停止
     */
    void callStop();

    /**
     * 调用重置
     */
    void callReset();

    /**
     * 调用销毁
     */
    void callRelease();
}
