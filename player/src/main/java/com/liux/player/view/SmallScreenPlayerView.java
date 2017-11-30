package com.liux.player.view;

import android.graphics.SurfaceTexture;
import android.view.Gravity;
import android.view.Surface;
import android.view.TextureView;

import com.liux.player.util.ViewUtil;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Created by Liux on 2017/11/21.
 */

public class SmallScreenPlayerView extends AbstractRenderView<TextureView> {

    private Surface mSurface;

    public SmallScreenPlayerView(AbstractPlayerView playerView) {
        super(playerView.getContext());

        int width = getResources().getDisplayMetrics().widthPixels / 2;
        int height = (int) (width * 0.67F);
        LayoutParams layoutParams = new LayoutParams(width, height);
        layoutParams.gravity = Gravity.RIGHT | Gravity.BOTTOM;
        layoutParams.rightMargin = ViewUtil.dp2px(getContext(), 10.0F);
        layoutParams.bottomMargin = (int) (layoutParams.rightMargin * 1.5F);
        ViewUtil.installToContentView(playerView, this, layoutParams);
    }

    @Override
    protected TextureView initView() {
        setControlView(new SmallScreenControlView(this));

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
}
