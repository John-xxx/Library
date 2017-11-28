package com.liux.player.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by Liux on 2017/11/24.
 */

public abstract class AbstractControlView extends FrameLayout {

    public AbstractControlView(@NonNull Context context) {
        super(context);
    }

    public AbstractControlView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AbstractControlView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AbstractControlView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * 载入媒体
     */
    public abstract void loadMedia();

    /**
     * 开始解析媒体
     */
    public abstract void prepare();

    /**
     * 解析媒体完成
     */
    public abstract void prepared();

    /**
     * 播放出错
     * @param error
     */
    public abstract void error(int error);

    /**
     * 开始播放
     */
    public abstract void playStart();

    /**
     * 播放完毕
     */
    public abstract void playComplete();



    /**
     * 播放焦点改变(组播事件)
     */
    public abstract void changeFocus();

    /**
     * 屏幕状态改变(正常/全屏/小屏)
     */
    public abstract void changeScreen();



    /**
     * 调用开始
     */
    public abstract void callStart();

    /**
     * 调用暂停
     */
    public abstract void callPause();

    /**
     * 调用停止
     */
    public abstract void callStop();

    /**
     * 调用重置
     */
    public abstract void callReset();

    /**
     * 调用销毁
     */
    public abstract void callRelease();



    /**
     * 缓存开始
     */
    public abstract void bufferStart();

    /**
     * 缓存结束
     */
    public abstract void bufferEnd();

    /**
     * 缓存改变
     * @param percent
     */
    public abstract void bufferUpdate(int percent);



    /**
     * 快退进开始
     */
    public abstract void seekStart();

    /**
     * 快退进结束
     */
    public abstract void seekEnd();
}
