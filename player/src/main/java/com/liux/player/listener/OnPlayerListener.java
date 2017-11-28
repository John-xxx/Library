package com.liux.player.listener;

import android.widget.ImageView;

import com.liux.player.Media;

/**
 * 留给开发者的播放器状态回调
 * Created by Liux on 2017/9/17.
 */

public interface OnPlayerListener {

    /**
     * 载入媒体
     */
    void loadMedia();

    /**
     * 播放出错
     * @param error
     */
    void error(int error);

    /**
     * 开始播放
     */
    void playStart();

    /**
     * 播放完毕
     */
    void playComplete();
}
