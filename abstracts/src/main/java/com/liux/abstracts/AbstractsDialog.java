package com.liux.abstracts;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialog;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

/**
 * 基础全屏沉浸式的 Dialog <br>
 * Created by Liux on 2017/8/23.
 */

public abstract class AbstractsDialog extends AppCompatDialog {

    private boolean mFullScreen = false;
    private ColorDrawable mBackground = new ColorDrawable(Color.TRANSPARENT);

    public AbstractsDialog(@NonNull Context context) {
        this(context, 0);
    }

    public AbstractsDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);

        openTranslucentMode();
    }

    public AbstractsDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        this(context);
        setCancelable(cancelable);
        setOnCancelListener(cancelListener);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();

        if (mFullScreen) {
            getWindow().setBackgroundDrawable(mBackground);
            getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }

    /**
     * 是否全屏模式
     * @return
     */
    public boolean isFullScreen() {
        return mFullScreen;
    }

    /**
     * 设置是否全屏模式
     * @param fullScreen
     * @return
     */
    public AbstractsDialog setFullScreen(boolean fullScreen) {
        mFullScreen = fullScreen;
        return this;
    }

    /**
     * 获取全屏背景色
     * @return
     */
    public int getBackgroundColor() {
        return mBackground.getColor();
    }

    /**
     * 设置全屏背景色
     * @param color
     * @return
     */
    public AbstractsDialog setBackgroundColor(int color) {
        mBackground = new ColorDrawable(color);
        return this;
    }

    /**
     * 仿照 Activity 开启状态栏沉浸式
     */
    private void openTranslucentMode() {
        getDelegate().requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }
}
