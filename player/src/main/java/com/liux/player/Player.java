package com.liux.player;

import com.liux.player.listener.OnPlayerListener;
import com.liux.player.view.ControlView;
import com.liux.player.view.PlayerView;
import com.liux.player.view.RenderView;

/**
 * Created by Liux on 2017/9/16.
 */

public interface Player {

    /**
     * 载入媒体资源
     * @param media
     */
    void loadMedia(Media media);

    /**
     * 开始播放
     */
    void start();

    /**
     * 暂停播放
     */
    void pause();

    /**
     * 停止播放器
     */
    void stop();

    /**
     * 重置播放器
     */
    void reset();

    /**
     * 释放播放器
     */
    void release();

    /**
     * 是否正在播放
     * @return
     */
    boolean playing();

    /**
     * 转跳至某点
     * @param pos
     */
    void seekTo(int pos);

    /**
     * 获取当前播放位置
     * @return
     */
    int getCurrentPosition();

    /**
     * 获取总长度
     * @return
     */
    int getDuration();

    /**
     * 获取以缓冲的百分比
     * @return
     */
    int getBufferPercentage();

    /**
     * 获取当前使用的音频会话ID
     * @return
     */
    int getAudioSessionId();

    /**
     * 是否可以暂停
     * @return
     */
    boolean canPause();

    /**
     * 是否可以前进
     * @return
     */
    boolean canSeekForward();

    /**
     * 是否可以后退
     * @return
     */
    boolean canSeekBackward();

    /**
     * 设置播放UI
     * @param view
     */
    void setPlayerView(PlayerView view);

    /**
     * 设置渲染UI
     * @param view
     */
    void setRenderView(RenderView view);

    /**
     * 设置控制UI
     * @param view
     */
    void setControlView(ControlView view);

    /**
     * 设置播放状态监听器
     * @param listener
     */
    void setOnPlayerListener(OnPlayerListener listener);
}
