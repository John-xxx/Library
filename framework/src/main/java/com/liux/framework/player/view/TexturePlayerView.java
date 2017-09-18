package com.liux.framework.player.view;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.view.TextureView;
import android.view.View;

import com.liux.framework.player.util.MeasureHelper;

/**
 * Created by Liux on 2017/9/16.
 */

public class TexturePlayerView extends PlayerView {

    private TextureView mTextureView;
    private MeasureHelper mMeasureHelper;
    private RenderCallback mRenderCallback;

    public TexturePlayerView(@NonNull Context context) {
        super(context);
    }

    public TexturePlayerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TexturePlayerView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TexturePlayerView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected View initRenderView() {
        mTextureView = new TextureView(getContext()) {
            @Override
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                mMeasureHelper.doMeasure(widthMeasureSpec,heightMeasureSpec);
                setMeasuredDimension(mMeasureHelper.getMeasuredWidth(), mMeasureHelper.getMeasuredHeight());
            }
        };
        mMeasureHelper = new MeasureHelper(mTextureView);

        mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                mRenderCallback.created(surface);
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                mRenderCallback.destroyed(surface);
                return true;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        });
        return mTextureView;
    }

    @Override
    public void setRenderCallback(RenderCallback callback) {
        mRenderCallback = callback;
    }

    @Override
    public void setAspectRatio(int aspectRatio) {
        mMeasureHelper.setAspectRatio(aspectRatio);
        mTextureView.requestLayout();
    }

    @Override
    public void setVideoRotation(int angle) {
        mMeasureHelper.setVideoRotation(angle);
        mTextureView.requestLayout();
    }

    @Override
    public void setVideoSize(int videoWidth, int videoHeight) {
        if (videoWidth > 0 && videoHeight > 0) {
            mMeasureHelper.setVideoSize(videoWidth, videoHeight);
            mTextureView.requestLayout();
        }
    }

    @Override
    public void setVideoSampleAspectRatio(int videoSarNum, int videoSarDen) {
        mMeasureHelper.setVideoSampleAspectRatio(videoSarNum, videoSarDen);
        mTextureView.requestLayout();
    }
}
