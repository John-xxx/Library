package com.liux.player.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

/**
 * Created by Liux on 2017/10/26.
 */

public class ViewUtil {

    /**
     * 从当前布局开始向上查找根内容布局
     * @param view
     * @return
     */
    public static FrameLayout getContentView(View view) {
        ViewParent parent = view.getParent();
        while (parent != null && parent instanceof ViewGroup) {
            if (((ViewGroup) parent).getId() == Window.ID_ANDROID_CONTENT) {
                return (FrameLayout) parent;
            } else {
                parent = parent.getParent();
            }
        }
        return null;
    }

    /**
     * 快捷开启全屏模式
     * @param context
     */
    public static void openFullScreen(Context context) {
        hideStatusBar(context);
        hideActionBar(context);
        hideNavigationBar(context);
    }

    /**
     * 快捷关闭全屏模式
     * @param context
     */
    public static void closeFullScreen(Context context) {
        showStatusBar(context);
        showActionBar(context);
        showNavigationBar(context);
    }

    /**
     * 显示标题栏
     * @param context
     */
    @SuppressLint("RestrictedApi")
    public static void showActionBar(Context context) {
        AppCompatActivity appCompatActivity = getAppCompActivity(context);
        if (appCompatActivity != null) {
            ActionBar actionBar = appCompatActivity.getSupportActionBar();
            if (actionBar != null) {
                actionBar.setShowHideAnimationEnabled(false);
                actionBar.show();
            }
        }
    }

    /**
     * 隐藏标题栏
     * @param context
     */
    @SuppressLint("RestrictedApi")
    public static void hideActionBar(Context context) {
        AppCompatActivity appCompatActivity = getAppCompActivity(context);
        if (appCompatActivity != null) {
            ActionBar actionBarb = appCompatActivity.getSupportActionBar();
            if (actionBarb != null) {
                actionBarb.setShowHideAnimationEnabled(false);
                actionBarb.hide();
            }
        }
    }

    /**
     * 显示状态栏
     * @param context
     */
    public static void showStatusBar(Context context) {
        if (context instanceof Activity){
            ((Activity) context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    /**
     * 隐藏状态栏
     * @param context
     */
    public static void hideStatusBar(Context context) {
        if (context instanceof Activity){
            ((Activity) context).getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    /**
     * 显示导航栏
     * @param context
     */
    public static void showNavigationBar(Context context) {
        if (!(context instanceof Activity)) return;
        Activity activity = (Activity) context;
        int systemUiVisibility = activity.getWindow().getDecorView().getSystemUiVisibility();
        systemUiVisibility = systemUiVisibility & ~View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            systemUiVisibility = systemUiVisibility & ~View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            systemUiVisibility = systemUiVisibility & ~View.SYSTEM_UI_FLAG_IMMERSIVE;
        }
        activity.getWindow().getDecorView().setSystemUiVisibility(systemUiVisibility);
    }

    /**
     * 隐藏导航栏
     * @param context
     */
    public static void hideNavigationBar(Context context) {
        if (!(context instanceof Activity)) return;
        Activity activity = (Activity) context;
        int systemUiVisibility = activity.getWindow().getDecorView().getSystemUiVisibility();
        systemUiVisibility = systemUiVisibility | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            systemUiVisibility = systemUiVisibility | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            systemUiVisibility = systemUiVisibility | View.SYSTEM_UI_FLAG_IMMERSIVE;
        }
        activity.getWindow().getDecorView().setSystemUiVisibility(systemUiVisibility);
    }

    /**
     * 尝试获取 AppCompatActivity
     * @param context
     * @return
     */
    public static AppCompatActivity getAppCompActivity(Context context) {
        if (context == null) return null;
        if (context instanceof AppCompatActivity) {
            return (AppCompatActivity) context;
        } else if (context instanceof ContextThemeWrapper) {
            return getAppCompActivity(((ContextThemeWrapper) context).getBaseContext());
        }
        return null;
    }

    /**
     * 判断是否水平
     * @param activity
     * @return
     */
    public static boolean isScreenHorizontal(Activity activity) {
        return activity.getWindowManager().getDefaultDisplay().getRotation() == Surface.ROTATION_90 ||
                activity.getWindowManager().getDefaultDisplay().getRotation() == Surface.ROTATION_270;
    }

    /**
     * 格式化时间
     * @param time
     * @return
     */
    public static String parseTime(int time) {
        StringBuilder builder = new StringBuilder();
        time = time / 1000;
        if (time > 3600) {
            int hour = time / 3600;
            builder.append(hour).append('∶');
            time = time % 3600;
        }
        if (time > 60) {
            int min = time / 60;
            if (min < 10) builder.append(0);
            builder.append(min).append('∶');
            time = time % 60;
        } else {
            builder.append("00∶");
        }
        if (time >= 0) {
            int min = time;
            if (min < 10) builder.append(0);
            builder.append(min);
        }
        return builder.toString();
    }

    /**
     * 获取屏幕亮度
     * @param context
     * @return
     */
    public static float getScreenBrightness(Context context) {
        float systemBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
        try {
            if (context instanceof Activity) {
                Window window = ((Activity) context).getWindow();
                systemBrightness = window.getAttributes().screenBrightness;
            }
            if (systemBrightness == WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE) {
                systemBrightness = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS) / 255.0f;
            }
        } catch (Exception e) {
            systemBrightness = 0;
        }
        return systemBrightness;
    }

    /**
     * 插入指定 View 到根内容布局
     * @param source
     * @param view
     */
    public static void installToContentView(View source, View view) {
        installToContentView(source, view, null);
    }

    /**
     * 插入指定 View 到根内容布局
     * @param target
     */
    public static void installToContentView(View source, View target, FrameLayout.LayoutParams layoutParams) {
        if (layoutParams == null) {
            layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
        ViewGroup viewGroup = getContentView(source);
        viewGroup.addView(target, layoutParams);
    }

    /**
     * 移除 View 在根内容布局
     * @param view
     */
    public static void uninstallToContentView(View view) {
        ViewGroup viewGroup = getContentView(view);
        viewGroup.removeView(view);
    }

    /**
     * 根据屏幕旋转角度获得方向
     * @param activity
     * @return
     */
    public static int getOrientationForRotation(Activity activity) {
        int orientation;
        switch (activity.getWindowManager().getDefaultDisplay().getRotation()) {
            case Surface.ROTATION_0:
                orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                break;
            case Surface.ROTATION_90:
                orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                break;
            case Surface.ROTATION_180:
                orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                break;
            case Surface.ROTATION_270:
                orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                break;
            default:
                orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                break;
        }
        return orientation;
    }

    /**
     * 获取屏幕方向
     * @param activity
     * @return
     */
    public static int getRequestedOrientation(Activity activity) {
        return activity.getRequestedOrientation();
    }

    /**
     * 设置屏幕方向
     * @param activity
     * @param requestedOrientation
     */
    public static void setRequestedOrientation(Activity activity, int requestedOrientation) {
        activity.setRequestedOrientation(requestedOrientation);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
