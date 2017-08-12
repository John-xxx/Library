package com.liux.framework.base;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.liux.framework.R;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.IllegalFormatFlagsException;
import java.util.Map;

/**
 * 基础Activity,提供以下能力 <br>
 * 1.自动隐藏输入法 {@link #setHandlerTouch(boolean)} <br>
 * 2.重定义创建细节生命周期 {@link #onCreate(Bundle, Intent)} {@link #onInitData(Bundle, Intent)} {@link #onInitView(Bundle)} {@link #onLazyLoad()} <br>
 * 3.实现任意数据的"意外"恢复和存储 {@link #onRestoreData(Map)} {@link #onSaveData(Map)} <br>
 * 4.实现沉浸式状态栏和一套自定义{@link TitleBar} {@link #onInitTitleBar()} {@link TransparentTitleBar} {@link DefaultTitleBar} <br>
 * Created by Liux on 2017/8/7
 */

public abstract class BaseActivity extends AppCompatActivity {
    private String TAG = "BaseActivity";

    /* ============== 生命周期_Begin ============== */

    // http://blog.csdn.net/javazejian/article/details/51932554

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 设置TitleBar控件
        mTitleBar = onInitTitleBar();

        onCreate(savedInstanceState, getIntent());

        if (mTitleBar != null) {
            mTitleBar.initView();
        }

        try {
            Map<String, Object> data = (Map<String, Object>) getLastCustomNonConfigurationInstance();
            if (data != null && !data.isEmpty()) {
                onRestoreData(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        onInitData(savedInstanceState, getIntent());

        onInitView(savedInstanceState);

        onLazyLoad();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        Map<String, Object> data = new HashMap<String, Object>();
        onSaveData(data);
        if (data.isEmpty()) {
            return null;
        }
        return data;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /* ============== 生命周期_End ============== */



    /* ============== 其他回调_Begin ============== */

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
        if (mTitleBar != null) {
            mTitleBar.setTitle(title.toString());
        }
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /* ============== 其他回调_End ============== */



    /* ============== 拦截点击相关_Begin ============== */

    private boolean mHandlerTouch = true;

    /**
     * 控制是否拦截点击事件,默认拦截 <br>
     * 若不需要拦截请复写该方法
     */
    protected void setHandlerTouch(boolean handlerTouch) {
        mHandlerTouch = handlerTouch;
    }

    /**
     * 监听点击事件,如果点击的是空白位置则隐藏软键盘
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mHandlerTouch) return super.onTouchEvent(event);
        if (null != this.getCurrentFocus()) {
            hideKeyboard(this.getCurrentFocus().getWindowToken());
        }
        return super.onTouchEvent(event);
    }

    /**
     * 监听事件分发,如果点击的是空白位置则隐藏软键盘
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!mHandlerTouch) return super.dispatchTouchEvent(ev);
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideKeyboard(v, ev)) {
                hideKeyboard(v.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时则不能隐藏
     *
     * @param v
     * @param event
     * @return
     */
    private boolean isShouldHideKeyboard(View v, MotionEvent event) {
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完
        // 第一个焦点不在EditText上，和用户用轨迹球选择其他的焦点
        if (v != null && (v instanceof EditText)) {
            int[] location = {0, 0};
            v.getLocationInWindow(location);
            int left = location[0], top = location[1], right = left + v.getWidth(), bottom = top + v.getHeight();
            // 如果是点击EditText的事件，忽略它。
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取InputMethodManager，隐藏软键盘
     * @param token
     */
    private void hideKeyboard(IBinder token) {
        if (token != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /* ============== 拦截点击相关_End ============== */



    /* ============== 数据和View_Begin ============== */

    /**
     * 系统初始化 {@link #onInitTitleBar()} 后调用
     * @param savedInstanceState
     */
    protected abstract void onCreate(@Nullable Bundle savedInstanceState, Intent intent);

    /**
     * 子类调用 {@link #onCreate(Bundle, Intent)} 后调用
     * @param savedInstanceState
     * @param intent
     */
    protected abstract void onInitData(@Nullable Bundle savedInstanceState, Intent intent);

    /**
     * 调用 {@link #onInitData(Bundle, Intent)} 后调用
     * @param savedInstanceState
     */
    protected abstract void onInitView(@Nullable Bundle savedInstanceState);

    /**
     * 懒加载模式, {@link #onCreate(Bundle)} 最后一个函数
     */
    protected abstract void onLazyLoad();

    /**
     * 子类调用 {@link #onCreate(Bundle)} 后调用 <br>
     * 使用 {@link #getLastCustomNonConfigurationInstance()}
     * @param data
     */
    protected abstract void onRestoreData(Map<String, Object> data);

    /**
     * {@link #onRetainCustomNonConfigurationInstance()} 后调用
     * @param data
     */
    protected abstract void onSaveData(Map<String, Object> data);

    /* ============== 数据和View_End ============== */



    /* ============== TitleBar_Begin ============== */

    private TitleBar mTitleBar;

    /**
     * {@link #onCreate(Bundle)} 后调用 <br>
     * 复写此方法实现自定义ToolBar
     * @return
     */
    protected TitleBar onInitTitleBar() {
        return new DefaultTitleBar(this);
    }

    /**
     * {@link #onTitleChanged(CharSequence, int)} 后调用 <br>
     * 获取当前使用的TitleBar
     * @return
     */
    protected TitleBar getTitleBar() {
        return mTitleBar;
    }

    /**
     * 自定义TitleBar需要实现此接口 <br>
     * 调用时机: <br>
     * 1.{@link #onCreate(Bundle)} <br>
     * 2.{@link #onInitTitleBar} to {@link TitleBar} <br>
     * 3.{@link #onCreate(Bundle, Intent)} <br>
     * 4.{@link TitleBar#initView} <br>
     * 5.{@link #onTitleChanged(CharSequence, int)} <br>
     * 6.{@link TitleBar#setTitle(String)}
     */
    public interface TitleBar {

        void initView();

        void setTitle(String title);
    }

    /**
     * 透明的{@link TitleBar}实现,只会透明状态栏 <br>
     *
     * http://www.jianshu.com/p/f02abf30fc82 <br>
     * http://www.jianshu.com/p/140be70b84cd <br>
     * https://juejin.im/post/5989ded56fb9a03c3b6c8bde
     */
    public static class TransparentTitleBar implements TitleBar {
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
            try {
                int darkModeFlag = 0;
                Class<? extends Window> clazz = activity.getWindow().getClass();
                Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                extraFlagField.invoke(activity.getWindow(), darkmode ? darkModeFlag : 0, darkModeFlag);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
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

    /**
     * 默认的自定义{@link TitleBar}实现 <br>
     * 在 {@link Window#ID_ANDROID_CONTENT} 布局下插入一个线性布局, <br>
     * 并将 DefaultTitleBar 和原来的子布局插入/移动到线性布局 <br>
     *
     * http://blog.csdn.net/yewei02538/article/details/60979075
     */
    public static class DefaultTitleBar extends TransparentTitleBar {
        private View mTitleBar, mBack, mMore;
        private TextView mTitle, mBackText, mMoreText;
        private ImageView mBackImage, mMoreImage;

        private OnTitleBarListener mOnTitleBarListener;

        private View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = v.getId();
                if (i == R.id.view_titlebar_default_back) {
                    if (mOnTitleBarListener == null || !mOnTitleBarListener.onBack()) {
                        getActivity().onBackPressed();
                    }
                } else if (i == R.id.view_titlebar_default_more) {
                    if (mOnTitleBarListener == null || !mOnTitleBarListener.onMore()) {

                    }
                }
            }
        };

        public DefaultTitleBar(AppCompatActivity activity) {
            super(activity);

            // 默认开启自适应输入法模式
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }

        @Override
        public void initView(int topPadding) {
            super.initView(topPadding);

            AppCompatActivity activity = getActivity();

            FrameLayout content = getContent();

            LinearLayout content_chlid = new LinearLayout(activity);
            content_chlid.setOrientation(LinearLayout.VERTICAL);

            mTitleBar = LayoutInflater.from(activity).inflate(R.layout.view_titlebar_default, content_chlid, false);
            mTitleBar.setPadding(
                    mTitleBar.getPaddingLeft(),
                    topPadding,
                    mTitleBar.getPaddingRight(),
                    mTitleBar.getPaddingBottom()
            );
            content_chlid.addView(mTitleBar);

            for (int i = 0; i < content.getChildCount(); i++) {
                View view = content.getChildAt(i);
                content.removeView(view);
                content_chlid.addView(view);
            }

            content.addView(content_chlid);

            mBack = mTitleBar.findViewById(R.id.view_titlebar_default_back);
            mMore = mTitleBar.findViewById(R.id.view_titlebar_default_more);
            mTitle = (TextView) mTitleBar.findViewById(R.id.view_titlebar_default_title);
            mBackText = (TextView) mTitleBar.findViewById(R.id.view_titlebar_default_back_text);
            mMoreText = (TextView) mTitleBar.findViewById(R.id.view_titlebar_default_more_text);
            mBackImage = (ImageView) mTitleBar.findViewById(R.id.view_titlebar_default_back_image);
            mMoreImage = (ImageView) mTitleBar.findViewById(R.id.view_titlebar_default_more_image);

            mBack.setOnClickListener(mOnClickListener);
            mMore.setOnClickListener(mOnClickListener);

//            TypedValue typedValue = new TypedValue();
//
//            activity.getTheme().resolveAttribute(R.attr.colorPrimaryDark, typedValue, true);
//            mTitleBar.setBackgroundColor(typedValue.data);
//
//            activity.getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
//            ((ViewGroup) mTitleBar).getChildAt(0).setBackgroundColor(typedValue.data);
        }

        @Override
        public void setTitle(String title) {
            super.setTitle(title);

            mTitle.setText(title);
        }

        public DefaultTitleBar hasBack(boolean has) {
            mBack.setVisibility(has ? View.VISIBLE : View.GONE);
            return this;
        }

        public DefaultTitleBar hasMore(boolean has) {
            mMore.setVisibility(has ? View.VISIBLE : View.GONE);
            return this;
        }

        public DefaultTitleBar setOnTitleBarListener(OnTitleBarListener listener) {
            mOnTitleBarListener = listener;
            return this;
        }

        public View getBack() {
            return mBack;
        }

        public View getMore() {
            return mMore;
        }

        public ImageView getBackIcon() {
            return mBackImage;
        }

        public ImageView getMoreIcon() {
            return mMoreImage;
        }

        public TextView getBackText() {
            return mBackText;
        }

        public TextView getMoreText() {
            return mMoreText;
        }

        public interface OnTitleBarListener {

            /**
             * 返回事件触发 <br>
             * @return 是否已处理,否则调用 {@link #onBackPressed()}
             */
            boolean onBack();

            /**
             * 更多事件处理 <br>
             * @return 是否已处理
             */
            boolean onMore();
        }
    }

    /* ============== TitleBar_End ============== */
}
