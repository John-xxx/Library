package com.liux.player.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * Created by Liux on 2017/11/24.
 */

public abstract class AbstractThumbView extends FrameLayout {
    private ImageView mImageView;

    public AbstractThumbView(@NonNull Context context) {
        super(context);
    }

    public AbstractThumbView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AbstractThumbView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AbstractThumbView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * 获取缩略图容器
     * @return
     */
    protected ImageView getImageView() {
        if (mImageView == null) {
            mImageView = initView();
        }
        return mImageView;
    }

    /**
     * 重置缩略图状态
     */
    protected void reset() {
        Drawable drawable = getImageView().getDrawable();
        if (drawable != null && drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            Bitmap bitmap = bitmapDrawable.getBitmap();
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
    }

    /**
     * 显示缩略图
     */
    protected void show() {
        getImageView().setVisibility(VISIBLE);
    }

    /**
     * 隐藏缩略图
     */
    protected void hide() {
        getImageView().setVisibility(GONE);
    }

    protected abstract ImageView initView();
}
