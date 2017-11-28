package com.liux.player.listener;

/**
 * Created by Liux on 2017/11/26.
 */

public interface OnSeekListener {

    /**
     * 快退进开始
     */
    void seekStart();

    /**
     * 快退进结束
     */
    void seekEnd();
}
