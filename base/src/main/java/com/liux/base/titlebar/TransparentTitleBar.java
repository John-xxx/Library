package com.liux.base.titlebar;

import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.IllegalFormatFlagsException;

/**
 * 透明的{@link TitleBar}实现,只会透明状态栏 <br>
 *
 * http://www.jianshu.com/p/f02abf30fc82 <br>
 * http://www.jianshu.com/p/140be70b84cd <br>
 * https://juejin.im/post/5989ded56fb9a03c3b6c8bde
 */
public class TransparentTitleBar implements TitleBar {
    private FrameLayout mContent;
    private AppCompatActivity mActivity;

    public TransparentTitleBar(AppCompatActivity activity) {
        mActivity = activity;

        // 设置AppCompatActivity无ToolBar
        activity.getDelegate().requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 设置状态栏和导航栏完全透明
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 清除 KITKAT 的透明设定
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

            // 全屏状态，视觉上的作用和WindowManager.LayoutParams.FLAG_FULLSCREEN一样
            // View.SYSTEM_UI_FLAG_FULLSCREEN
            // 这两个属性并不会真正隐藏状态栏或者导航栏，只是把整个content的可布局区域延伸到了其中
            // View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            // View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            // 此View一般和上面几个提到的属性一起使用，它可以保证在系统控件隐藏显示时，不会让本view重新layout
            // View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

            // 开启系统状态栏颜色设置
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            // 设定系统状态栏颜色
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
            // activity.getWindow().setNavigationBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    @Override
    public final void initView() {
        ActionBar actionBar = mActivity.getSupportActionBar();
        if (actionBar != null) {
            throw new IllegalFormatFlagsException("The window style do not contain Window.FEATURE_NO_TITLE");
        }

        TransparentAndResizeFix.install(getActivity());

        mContent = (FrameLayout) getActivity().findViewById(Window.ID_ANDROID_CONTENT);

        int topPadding = getTransparentStatusBarHeight();
        initView(topPadding);
    }

    @Override
    public void setTitle(String title) {

    }

    @Override
    public void setTitleColor(int color) {

    }

    @Override
    public View getStatusBar() {
        return null;
    }

    @Override
    public View getTitleBar() {
        return null;
    }

    /**
     * 初始化 TitleBar 的View <br>
     * {@link #initView()} 后调用
     * @param topPadding
     */
    public void initView(int topPadding) {

    }

    public AppCompatActivity getActivity() {
        return mActivity;
    }

    public FrameLayout getContent() {
        return mContent;
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
        AppCompatActivity activity = getActivity();
        if (setMiuiStatusBarMode(activity, darkmode)) return true;
        if (setMeizuStatusBarMode(activity, darkmode)) return true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = activity.getWindow().getDecorView();
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

    /**
     * {@link View#SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN} <br>
     * {@link WindowManager.LayoutParams#SOFT_INPUT_ADJUST_RESIZE} <br>
     * 全屏/沉浸式状态栏下，各种键盘挡住输入框解决办法 <br>
     * http://blog.csdn.net/qq_24531461/article/details/71412623
     */
    public static class TransparentAndResizeFix implements ViewTreeObserver.OnGlobalLayoutListener {
        private int mLastHeight;
        private ViewGroup mViewGroup;
        private ViewGroup.LayoutParams mLayoutParams;

        public static void install(AppCompatActivity activity) {
            if (activity.getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE) return;
            if ((activity.getWindow().getDecorView().getSystemUiVisibility() & View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN) != View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN) return;
            new TransparentAndResizeFix(activity);
        }

        private TransparentAndResizeFix(AppCompatActivity activity) {
            View content = activity.findViewById(Window.ID_ANDROID_CONTENT);

            mViewGroup = (ViewGroup) content.getParent();
            mLayoutParams = content.getLayoutParams();

            content.getViewTreeObserver().addOnGlobalLayoutListener(this);
        }

        @Override
        public void onGlobalLayout() {
            // 获取Window可视高度
            Rect rect = new Rect();
            mViewGroup.getWindowVisibleDisplayFrame(rect);
            int window_height = rect.bottom;

            if (window_height != mLastHeight) {
                mLastHeight = window_height;

                // 获取内容区域可视高度
                mViewGroup.getDrawingRect(rect);
                int content_height = rect.bottom;

                // 取Window和内容区域差异高度
                int diff_height = content_height - window_height;

                if (diff_height > (content_height / 6)) {
                    mLayoutParams.height = content_height - diff_height;
                } else {
                    mLayoutParams.height = content_height;
                }

                // 请求更新布局
                mViewGroup.requestLayout();
            }
        }
    }
}