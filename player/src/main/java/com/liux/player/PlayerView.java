package com.liux.player;

import android.widget.MediaController;

import com.liux.player.listener.OnBufferListener;
import com.liux.player.listener.OnOperateListener;
import com.liux.player.listener.OnPlayerListener;
import com.liux.player.listener.OnScreenListener;
import com.liux.player.listener.OnSeekListener;
import com.liux.player.listener.OnThumbnailListener;
import com.liux.player.view.AbstractControlView;
import com.liux.player.view.AbstractThumbView;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Created by Liux on 2017/10/25.
 */

public interface PlayerView extends MediaController.MediaPlayerControl {
    int INFO_UNKNOWN = IMediaPlayer.MEDIA_INFO_UNKNOWN;
    int INFO_STARTED_AS_NEXT = IMediaPlayer.MEDIA_INFO_STARTED_AS_NEXT;
    int INFO_VIDEO_RENDERING_START = IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START;
    int INFO_VIDEO_TRACK_LAGGING = IMediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING;
    int INFO_BUFFERING_START = IMediaPlayer.MEDIA_INFO_BUFFERING_START;
    int INFO_BUFFERING_END = IMediaPlayer.MEDIA_INFO_BUFFERING_END;
    int INFO_NETWORK_BANDWIDTH = IMediaPlayer.MEDIA_INFO_NETWORK_BANDWIDTH;
    int INFO_BAD_INTERLEAVING = IMediaPlayer.MEDIA_INFO_BAD_INTERLEAVING;
    int INFO_NOT_SEEKABLE = IMediaPlayer.MEDIA_INFO_NOT_SEEKABLE;
    int INFO_METADATA_UPDATE = IMediaPlayer.MEDIA_INFO_METADATA_UPDATE;
    int INFO_TIMED_TEXT_ERROR = IMediaPlayer.MEDIA_INFO_TIMED_TEXT_ERROR;
    int INFO_UNSUPPORTED_SUBTITLE = IMediaPlayer.MEDIA_INFO_UNSUPPORTED_SUBTITLE;
    int INFO_SUBTITLE_TIMED_OUT = IMediaPlayer.MEDIA_INFO_SUBTITLE_TIMED_OUT;
    int INFO_VIDEO_ROTATION_CHANGED = IMediaPlayer.MEDIA_INFO_VIDEO_ROTATION_CHANGED;
    int INFO_AUDIO_RENDERING_START = IMediaPlayer.MEDIA_INFO_AUDIO_RENDERING_START;
    int INFO_AUDIO_DECODED_START = IMediaPlayer.MEDIA_INFO_AUDIO_DECODED_START;
    int INFO_VIDEO_DECODED_START = IMediaPlayer.MEDIA_INFO_VIDEO_DECODED_START;
    int INFO_OPEN_INPUT = IMediaPlayer.MEDIA_INFO_OPEN_INPUT;
    int INFO_FIND_STREAM_INFO = IMediaPlayer.MEDIA_INFO_FIND_STREAM_INFO;
    int INFO_COMPONENT_OPEN = IMediaPlayer.MEDIA_INFO_COMPONENT_OPEN;
    int INFO_MEDIA_ACCURATE_SEEK_COMPLETE = IMediaPlayer.MEDIA_INFO_MEDIA_ACCURATE_SEEK_COMPLETE;

    int ERROR_UNKNOWN = IMediaPlayer.MEDIA_ERROR_UNKNOWN;
    int ERROR_SERVER_DIED = IMediaPlayer.MEDIA_ERROR_SERVER_DIED;
    int ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK = IMediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK;
    int ERROR_IO = IMediaPlayer.MEDIA_ERROR_IO;
    int ERROR_MALFORMED = IMediaPlayer.MEDIA_ERROR_MALFORMED;
    int ERROR_UNSUPPORTED = IMediaPlayer.MEDIA_ERROR_UNSUPPORTED;
    int ERROR_TIMED_OUT = IMediaPlayer.MEDIA_ERROR_TIMED_OUT;

    /**
     * 停止播放
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
     * 设置自定义缩略图视图
     * @param thumbView
     */
    void setThumbView(AbstractThumbView thumbView);

    /**
     * 设置自定义控制视图
     * @param controlView
     */
    void setControlView(AbstractControlView controlView);

    /**
     * 获取组播控制器
     * @return
     */
    PlayerGroup getPlayerGroup();

    /**
     * 设置组播控制器
     * @param group
     */
    void setPlayerGroup(PlayerGroup group);

    /**
     * 获取播放器内核类型
     * @return
     */
    Decoder getDecoder();

    /**
     * 设置播放器内核类型
     * @param type
     */
    void setDecoder(Decoder type);

    /**
     * 获取播放媒体
     * @return
     */
    Media getMedia();

    /**
     * 设置播放媒体
     * @param media
     */
    void setMedia(Media media);

    /**
     * 是否全屏
     * @return
     */
    boolean isFullScreen();

    /**
     * 设置全屏
     */
    void openFullScreen();

    /**
     * 设置全屏
     */
    void closeFullScreen();

    /**
     * 是否可以小窗
     * @return
     */
    boolean canSmallScreen();

    /**
     * 设置是否可以小窗
     * @param canSmallScreen
     */
    void setSmallScreen(boolean canSmallScreen);

    /**
     * 是否小窗
     * @return
     */
    boolean isSmallScreen();

    /**
     * 打开小窗
     */
    void openSmallScreen();

    /**
     * 关闭小窗
     */
    void closeSmallScreen();

    /**
     * 设置播放器事件监听器
     * @param listener
     */
    void setOnPlayerListener(OnPlayerListener listener);

    /**
     * 设置播放器事件监听器
     * @param listener
     */
    void setOnThumbnailListener(OnThumbnailListener listener);

    /**
     * 设置屏幕事件监听器
     * @param listener
     */
    void setOnScreenListener(OnScreenListener listener);

    /**
     * 设置操作时间监听器
     * @param listener
     */
    void setOnOperateListener(OnOperateListener listener);

    /**
     * 设置快退进事件监听器
     * @param listener
     */
    void setOnSeekListener(OnSeekListener listener);

    /**
     * 设置缓冲事件监听器
     * @param listener
     */
    void setOnBufferListener(OnBufferListener listener);

    /**
     * 是否处理返回事件
     * @return
     */
    boolean onBackPressed();

    /**
     * 解码器类型
     */
    enum Decoder {

        FFMPEG,

        ANDROID,

        EXO;
    }
}
