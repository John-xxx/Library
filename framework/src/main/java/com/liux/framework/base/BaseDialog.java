package com.liux.framework.base;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;

/**
 * 基础全屏沉浸式的 Dialog <br>
 * Created by Liux on 2017/8/23.
 */

public class BaseDialog extends Dialog {

    public BaseDialog(@NonNull Context context) {
        this(context, android.R.style.Theme_Translucent_NoTitleBar);
    }

    private BaseDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);

        openTranslucentMode();
    }

    public BaseDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        this(context);
        setCancelable(cancelable);
        setOnCancelListener(cancelListener);
    }

    /**
     * 仿照 Activity 开启状态栏沉浸式
     */
    private void openTranslucentMode() {
        // 设置状态栏和导航栏完全透明
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 清除 KITKAT 的透明设定
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

            // 全屏状态，视觉上的作用和WindowManager.LayoutParams.FLAG_FULLSCREEN一样
            // View.SYSTEM_UI_FLAG_FULLSCREEN
            // 这两个属性并不会真正隐藏状态栏或者导航栏，只是把整个content的可布局区域延伸到了其中
            // View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            // View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            // 此View一般和上面几个提到的属性一起使用，它可以保证在系统控件隐藏显示时，不会让本view重新layout
            // View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

            // 开启系统状态栏颜色设置
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            // 设定系统状态栏颜色
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            // getWindow().setNavigationBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }
}
