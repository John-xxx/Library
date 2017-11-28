package com.liux.player;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;

import com.liux.player.view.AbstractRenderView;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Created by Liux on 2017/9/16.
 */

public class TexturePlayerView extends AbstractRenderView<TextureView> {

    private Surface mSurface;

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
    protected TextureView initView() {
        TextureView textureView = new TextureView(getContext()) {
            @Override
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                getMeasureHelper().doMeasure(widthMeasureSpec,heightMeasureSpec);
                setMeasuredDimension(getMeasureHelper().getMeasuredWidth(), getMeasureHelper().getMeasuredHeight());
            }
        };

        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                mSurface = new Surface(surface);
                getRenderCallback().created();
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                mSurface = null;
                getRenderCallback().destroyed();
                return true;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        });

        return textureView;
    }

    @Override
    protected void bindRenderView(IMediaPlayer player) {
        player.setSurface(mSurface);
    }

//    @Override
//    protected void beforeChangeFullScreen() {
//        if (getRenderView().isAvailable()) {
//            mBitmap = getRenderView().getBitmap();
//        }
//    }
//
//    @Override
//    protected void afterChangeFullScreen() {
//        if (mBitmap != null && getRenderView().isAvailable()) {
//            Surface surface = new Surface(getRenderView().getSurfaceTexture());
//            Canvas canvas = surface.lockCanvas(null);
//            canvas.drawBitmap(
//                    mBitmap,
//                    new Rect(0, 0, mBitmap.getWidth(), mBitmap.getHeight()),
//                    new Rect(0, 0, canvas.getWidth(), canvas.getHeight()),
//                    null
//            );
//            surface.unlockCanvasAndPost(canvas);
//            surface.release();
//            mBitmap = null;
//        }
//    }
}
