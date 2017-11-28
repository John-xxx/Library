package com.liux.player;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.liux.player.view.AbstractRenderView;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Created by Liux on 2017/9/16.
 */

public class SurfacePlayerView extends AbstractRenderView<SurfaceView> {

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
    protected SurfaceView initView() {
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

//    @Override
//    protected void beforeChangeFullScreen() {
//        // TODO: 2017/10/28 截屏暂未实现
//        //mBitmap = Bitmap.createBitmap(mSurfaceView.getWidth(), mSurfaceView.getHeight(), Bitmap.Config.RGB_565);
//        //Canvas canvas = new Canvas(mBitmap);
//    }
//
//    @Override
//    protected void afterChangeFullScreen() {
//        if (mBitmap != null && getRenderView().getHolder().isCreating()) {
//            Canvas canvas = getRenderView().getHolder().lockCanvas(null);
//            canvas.drawBitmap(
//                    mBitmap,
//                    new Rect(0, 0, mBitmap.getWidth(), mBitmap.getHeight()),
//                    new Rect(0, 0, canvas.getWidth(), canvas.getHeight()),
//                    null
//            );
//            getRenderView().getHolder().unlockCanvasAndPost(canvas);
//            mBitmap = null;
//        }
//    }
}
