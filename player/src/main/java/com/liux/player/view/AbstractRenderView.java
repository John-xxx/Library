package com.liux.player.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.liux.player.util.MeasureHelper;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Created by Liux on 2017/11/24.
 */

public abstract class AbstractRenderView<T extends View> extends AbstractPlayerView {
    public static final int AR_ASPECT_FIT_PARENT = 0;
    public static final int AR_ASPECT_FILL_PARENT = 1;
    public static final int AR_ASPECT_WRAP_CONTENT = 2;
    public static final int AR_MATCH_PARENT = 3;
    public static final int AR_16_9_FIT_PARENT = 4;
    public static final int AR_4_3_FIT_PARENT = 5;

    private T mView;
    private MeasureHelper mMeasureHelper;

    public AbstractRenderView(@NonNull Context context) {
        super(context);
    }

    public AbstractRenderView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AbstractRenderView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AbstractRenderView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * 取测量辅助类
     * @return
     */
    protected MeasureHelper getMeasureHelper() {
        if (mMeasureHelper == null) {
            mMeasureHelper = new MeasureHelper(mView);
        }
        return mMeasureHelper;
    }

    /**
     * 获取渲染器显示View
     * @return
     */
    protected T getRenderView() {
        if (mView == null) {
            mView = initView();
        }
        return mView;
    }

    /**
     * 清除视图使之透明
     */
    protected void clear() {
        getMeasureHelper().doMeasure(0, 0);
        getRenderView().requestLayout();
    }

    /**
     * 设置视频尺寸
     * @param width
     * @param height
     */
    protected void setVideoSize(int width, int height) {
        if (width > 0 && height > 0) {
            mMeasureHelper.setVideoSize(width, height);
            getRenderView().requestLayout();
        }
    }

    /**
     * 设置显示长宽比
     * {@link #AR_ASPECT_FIT_PARENT}
     * {@link #AR_ASPECT_FILL_PARENT}
     * {@link #AR_ASPECT_WRAP_CONTENT}
     * {@link #AR_MATCH_PARENT}
     * {@link #AR_16_9_FIT_PARENT}
     * {@link #AR_4_3_FIT_PARENT}
     * @param ratio
     */
    protected void setAspectRatio(int ratio) {
        mMeasureHelper.setAspectRatio(ratio);
        getRenderView().requestLayout();
    }

    /**
     * 设置视频旋转角度
     * @param rotation
     */
    protected void setVideoRotation(int rotation) {
        mMeasureHelper.setVideoRotation(rotation);
        getRenderView().requestLayout();
    }

    /**
     * 设置视频纵横比
     * @param sarNum
     * @param sarDen
     */
    protected void setVideoSampleAspectRatio(int sarNum, int sarDen) {
        mMeasureHelper.setVideoSampleAspectRatio(sarNum, sarDen);
        getRenderView().requestLayout();
    }

    /**
     * 初始化渲染视图
     * @return
     */
    protected abstract T initView();

    /**
     * 绑定渲染器到内核
     * @param player
     */
    protected abstract void bindRenderView(IMediaPlayer player);

    /**
     * 渲染视图状态回调
     */
    public interface Callback {

        /**
         * 是否准备好
         * @return
         */
        boolean prepared();

        /**
         * 视图创建完毕
         */
        void created();

        /**
         * 视图销毁完毕
         */
        void destroyed();
    }
}
