package com.liux.base.titlebar;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 自定义TitleBar需要实现此接口 <br>
 * 调用时机: <br>
 * 1.{@link com.liux.base.BaseActivity#onCreate(Bundle)} <br>
 * 2.{@link com.liux.base.BaseActivity#onInitTitleBar} to {@link TitleBar} <br>
 * 3.{@link com.liux.base.BaseActivity#onCreate(Bundle, Intent)} <br>
 * 4.{@link #initView} <br>
 * 5.{@link com.liux.base.BaseActivity#onTitleChanged(CharSequence, int)} <br>
 * 6.{@link #setTitle(String)}
 */
public abstract class TitleBar {
    private AppCompatActivity mActivity;

    public TitleBar(AppCompatActivity activity) {
        mActivity = activity;
    }

    public abstract void initView();

    public abstract void setTitle(String title);

    public abstract void setTitleColor(int color);

    public AppCompatActivity getActivity() {
        return mActivity;
    }

    /**
     * 获取沉浸式状态下状态栏的高度 <br>
     * 小于 KITKAT 表示不支持开启沉浸式,返回 0
     * @return
     */
    public int getTransparentStatusBarHeight() {
        int result = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int resourceId = getActivity().getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                result = getActivity().getResources().getDimensionPixelSize(resourceId);
            }
        }
        return result;
    }

    /**
     *  设置系统状态栏图标和字体配色模式
     * @param darkmode 是否深色模式
     * @return 成功执行返回true
     */
    public boolean setStatusBarMode(boolean darkmode) {
        if (setMiuiStatusBarMode(getActivity(), darkmode)) return true;
        if (setMeizuStatusBarMode(getActivity(), darkmode)) return true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getActivity().getWindow().getDecorView();
            int ui = decor.getSystemUiVisibility();
            if (darkmode) {
                ui |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            } else {
                ui &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }
            decor.setSystemUiVisibility(ui);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 设置 MIUIV6+ 系统状态栏图标和字体配色模式 <br>
     * @param activity 需要设置的Activity
     * @param darkmode 是否把状态栏字体及图标颜色设置为深色
     * @return 成功执行返回true
     */
    private boolean setMiuiStatusBarMode(AppCompatActivity activity, boolean darkmode) {
        // 开发版 7.7.13 以前版本
        try {
            int darkModeFlag = 0;
            Class<? extends Window> clazz = activity.getWindow().getClass();
            Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod(darkmode ? "addExtraFlags" : "clearExtraFlags", int.class);
            extraFlagField.invoke(activity.getWindow(), darkModeFlag);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 开发版 7.7.13 及以后版本
        // http://www.miui.com/thread-8946673-1-1.html
        //try {
        //    Window window = activity.getWindow();
        //    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        //    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //    window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        //} catch (Exception e) {
        //    e.printStackTrace();
        //}
        return false;
    }

    /**
     * 设置 Flyme 系统状态栏图标和字体配色模式 <br>
     * @param activity 需要设置的Activity
     * @param darkmode 是否把状态栏字体及图标颜色设置为深色
     * @return 成功执行返回true
     */
    private boolean setMeizuStatusBarMode(AppCompatActivity activity, boolean darkmode) {
        try {
            WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
            Field darkFlag = WindowManager.LayoutParams.class.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
            Field meizuFlags = WindowManager.LayoutParams.class.getDeclaredField("meizuFlags");
            darkFlag.setAccessible(true);
            meizuFlags.setAccessible(true);
            int bit = darkFlag.getInt(null);
            int value = meizuFlags.getInt(lp);
            if (darkmode) {
                value |= bit;
            } else {
                value &= ~bit;
            }
            meizuFlags.setInt(lp, value);
            activity.getWindow().setAttributes(lp);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
