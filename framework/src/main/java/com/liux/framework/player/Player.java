package com.liux.framework.player;

import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.view.SurfaceHolder;
import android.widget.MediaController;

import com.liux.framework.player.listener.OnPlayerListener;
import com.liux.framework.player.view.ControlView;
import com.liux.framework.player.view.RenderView;

import java.util.Map;

/**
 * Created by Liux on 2017/9/16.
 */

public interface Player {

    /**
     * 载入文件,准备播放
     * @param path
     */
    void load(String path);

    /**
     * 载入uri,准备播放
     * @param uri
     */
    void load(Uri uri);

    /**
     * 载入uri和请求头,准备播放
     * @param uri
     */
    void load(Uri uri, Map<String, String> headers);

    /**
     * 开始播放
     */
    void start();

    /**
     * 暂停播放
     */
    void stop();

    /**
     * 停止/重置播放器
     */
    void reset();

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
     * 是否可以后退
     * @return
     */
    boolean canSeekBackward();

    /**
     * 是否可以前进
     * @return
     */
    boolean canSeekForward();

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
