package com.liux.framework.player.view;

import android.graphics.SurfaceTexture;
import android.view.SurfaceHolder;
import android.view.View;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * 播放渲染界面
 * Created by Liux on 2017/9/17.
 */

public interface RenderView {
    int AR_ASPECT_FIT_PARENT = 0;
    int AR_ASPECT_FILL_PARENT = 1;
    int AR_ASPECT_WRAP_CONTENT = 2;
    int AR_MATCH_PARENT = 3;
    int AR_16_9_FIT_PARENT = 4;
    int AR_4_3_FIT_PARENT = 5;

    /**
     * 获取渲染视图
     * @return
     */
    View getView();

    /**
     * 设置长宽比
     * @param ratio
     */
    void setAspectRatio(int ratio);

    /**
     * 设置角度
     * @param angle
     */
    void setVideoRotation(int angle);

    /**
     * 设置视频尺寸
     * @param videoWidth
     * @param videoHeight
     */
    void setVideoSize(int videoWidth, int videoHeight);

    /**
     * 设置视频纵横比
     * @param videoSarNum
     * @param videoSarDen
     */
    void setVideoSampleAspectRatio(int videoSarNum, int videoSarDen);

    /**
     * 渲染视图生命周期绑定
     */
    interface RenderCallback {

        void reset();

        boolean isCreated();

        void bindPlayer();

        void created(SurfaceHolder holder);

        void created(SurfaceTexture surface);

        void destroyed(SurfaceHolder holder);

        void destroyed(SurfaceTexture surface);
    }
}
