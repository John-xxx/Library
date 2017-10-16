package com.liux.player.view;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.liux.player.util.MeasureHelper;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Created by Liux on 2017/9/16.
 */

public class SurfacePlayerView extends AbstractPlayerView implements RenderView {

    private Callback mCallback;
    private SurfaceView mSurfaceView;
    private MeasureHelper mMeasureHelper;

    public SurfacePlayerView(@NonNull Context context) {
        super(context);
    }

    public SurfacePlayerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SurfacePlayerView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SurfacePlayerView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected RenderView initRenderView() {
        mSurfaceView = new SurfaceView(getContext()) {
            @Override
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                mMeasureHelper.doMeasure(widthMeasureSpec,heightMeasureSpec);
                setMeasuredDimension(mMeasureHelper.getMeasuredWidth(), mMeasureHelper.getMeasuredHeight());
            }
        };
        mMeasureHelper = new MeasureHelper(mSurfaceView);

        mSurfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_NORMAL);
        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                mCallback.created();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                mCallback.destroyed();
            }
        });
        return this;
    }

    @Override
    public View getView() {
        return mSurfaceView;
    }

    @Override
    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    @Override
    public void bindRender(IMediaPlayer player) {
        player.setDisplay(mSurfaceView.getHolder());
    }

    @Override
    public void setAspectRatio(int aspectRatio) {
        mMeasureHelper.setAspectRatio(aspectRatio);
        mSurfaceView.requestLayout();
    }

    @Override
    public void setVideoRotation(int angle) {
        mMeasureHelper.setVideoRotation(angle);
        mSurfaceView.requestLayout();
    }

    @Override
    public void setVideoSize(int videoWidth, int videoHeight) {
        if (videoWidth > 0 && videoHeight > 0) {
            mMeasureHelper.setVideoSize(videoWidth, videoHeight);
            mSurfaceView.requestLayout();
        }
    }

    @Override
    public void setVideoSampleAspectRatio(int videoSarNum, int videoSarDen) {
        mMeasureHelper.setVideoSampleAspectRatio(videoSarNum, videoSarDen);
        mSurfaceView.requestLayout();
    }
}
