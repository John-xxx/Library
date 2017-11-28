package com.liux.player.view;

import android.content.Context;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.liux.player.util.ViewUtil;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Created by Liux on 2017/11/21.
 */

public class FullScreenPlayerView extends AbstractRenderView<SurfaceView> {

    public FullScreenPlayerView(AbstractPlayerView playerView) {
        super(playerView.getContext());

        ViewUtil.installToContentView(playerView, this);
    }

    @Override
    protected SurfaceView initView() {
        setControlView(new FullScreenControlView(this));

        SurfaceView surfaceView = new SurfaceView(getContext()) {
            @Override
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                getMeasureHelper().doMeasure(widthMeasureSpec,heightMeasureSpec);
                setMeasuredDimension(getMeasureHelper().getMeasuredWidth(), getMeasureHelper().getMeasuredHeight());
            }
        };


        surfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_NORMAL);
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                getRenderCallback().created();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                getRenderCallback().destroyed();
            }
        });

        return surfaceView;
    }

    @Override
    protected void bindRenderView(IMediaPlayer player) {
        player.setDisplay(getRenderView().getHolder());
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ViewUtil.openFullScreen(getContext());
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ViewUtil.closeFullScreen(getContext());
    }
}
