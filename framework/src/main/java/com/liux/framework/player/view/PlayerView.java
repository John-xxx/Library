package com.liux.framework.player.view;

import android.net.Uri;
import android.view.View;

import com.liux.framework.player.Media;
import com.liux.framework.player.Player;
import com.liux.framework.player.listener.OnPlayerListener;

import java.util.Map;

/**
 * 播放器视图
 * Created by Liux on 2017/9/24.
 */

public interface PlayerView {

    /**
     * 获取播放视图
     * @return
     */
    View getView();

    /**
     * 设置播放媒体文件
     * @param media
     */
    void setMedia(String media);

    /**
     * 设置播放媒体文件
     * @param media
     * @param header
     */
    void setMedia(String media, Map<String, String> header);

    /**
     * 设置播放媒体文件
     * @param uri
     */
    void setMedia(Uri uri);

    /**
     * 设置播放媒体文件
     * @param uri
     * @param header
     */
    void setMedia(Uri uri, Map<String, String> header);

    /**
     * 设置播放媒体文件
     * @param media
     */
    void setMedia(Media media);

    /**
     * 获取播放媒体文件
     * @return
     */
    Media getMedia();

    /**
     * 获取播放控制器
     * @return
     */
    Player getPlayer();

    /**
     * 设置播放控制器,最好放在 setMedia 之前设置
     */
    void setPlayer(Player player);

    /**
     * 设置播放事件监听
     * @param listener
     */
    void setOnPlayerListener(OnPlayerListener listener);

    /**
     * 是否是全屏模式
     * @return
     */
    boolean getFullScreen();

    /**
     * 设置是否全屏
     * @param fullScreen
     */
    void setFullScreen(boolean fullScreen);
}
