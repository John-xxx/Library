package com.liux.player.listener;

/**
 * Created by Liux on 2017/11/26.
 */

public interface OnBufferListener {

    /**
     * 缓存开始
     */
    void bufferStart();

    /**
     * 缓存结束
     */
    void bufferEnd();

    /**
     * 缓存改变
     * @param percent
     */
    void bufferUpdate(int percent);
}
