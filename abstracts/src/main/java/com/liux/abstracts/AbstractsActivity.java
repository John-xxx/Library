package com.liux.abstracts;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.liux.abstracts.titlebar.DefaultTitleBar;
import com.liux.abstracts.titlebar.TitleBar;

import java.util.HashMap;
import java.util.Map;

/**
 * 基础Activity,提供以下能力 <br>
 * 1.自动隐藏输入法 {@link #setHandlerTouch(boolean)} <br>
 * 2.重定义创建细节生命周期 {@link #onCreate(Bundle, Intent)} {@link #onInitData(Bundle, Intent)} {@link #onInitView(Bundle)} {@link #onLazyLoad()} <br>
 * 3.实现任意数据的"意外"恢复和存储 {@link #onRestoreData(Map)} {@link #onSaveData(Map)} <br>
 * 4.实现沉浸式状态栏和一套自定义{@link TitleBar} {@link #onInitTitleBar()} {@link com.liux.abstracts.titlebar.TransparentTitleBar} {@link com.liux.abstracts.titlebar.DefaultTitleBar} <br>
 * 2017-8-21 <br>
 * 调整恢复数据的调用时机<br>
 * Created by Liux on 2017/8/7
 */

public abstract class AbstractsActivity extends AppCompatActivity {
    private String TAG = "AbstractsActivity";

    /* ============== 生命周期_Begin ============== */

    // http://blog.csdn.net/lvxiangan/article/details/8591536

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mTitleBar = onInitTitleBar();

        super.onCreate(savedInstanceState);

        onCreate(savedInstanceState, getIntent());

        if (mTitleBar != null) {
            mTitleBar.initView();
        }

        onInitData(savedInstanceState, getIntent());

        try {
            Map<String, Object> data = (Map<String, Object>) getLastCustomNonConfigurationInstance();
            if (data != null && !data.isEmpty()) {
                onRestoreData(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        onInitView(savedInstanceState);
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

    /**
     * 恢复数据 先于 {@link #onPostCreate(Bundle)} 调用
     * @param savedInstanceState
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        onLazyLoad();
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
            //mTitleBar.setTitleColor(color);
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
    private GestureDetector mGestureDetector;
    private InputMethodManager mInputMethodManager;

    private GestureDetector.SimpleOnGestureListener mSimpleOnGestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onSingleTapUp(MotionEvent event) {
            hideKeyboard(event);
            return true;
        }

//        @Override
//        public boolean onSingleTapConfirmed(MotionEvent event) {
//            hideKeyboard(event);
//            return true;
//        }
    };

    /**
     * 控制是否拦截点击事件,默认拦截 <br>
     * 若不需要拦截请复写该方法
     */
    protected void setHandlerTouch(boolean handlerTouch) {
        mHandlerTouch = handlerTouch;
    }

    /**
     * 监听事件分发,如果点击的是不是当前EditText则隐藏软键盘
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (!mHandlerTouch) return super.dispatchTouchEvent(event);
        getGestureDetector().onTouchEvent(event);
        return super.dispatchTouchEvent(event);
    }

    /**
     * 监听点击事件,如果点击的是空白位置则隐藏软键盘
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mHandlerTouch) return super.onTouchEvent(event);
        getGestureDetector().onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    private GestureDetector getGestureDetector() {
        if (mGestureDetector == null) {
            mGestureDetector = new GestureDetector(this, mSimpleOnGestureListener);
        }
        return mGestureDetector;
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
            if (event.getX() > left && event.getX() < right && event.getY() > top && event.getY() < bottom) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取InputMethodManager，隐藏软键盘
     * @param event
     */
    private void hideKeyboard(MotionEvent event) {
        View view = getCurrentFocus();
        if (view != null) {
            if (event != null && !isShouldHideKeyboard(view, event)) return;
            IBinder token = view.getWindowToken();
            if (token != null) {
                if (mInputMethodManager == null) {
                    mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                }
                mInputMethodManager.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
            }
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
     * 调用 {@link #onInitData(Bundle, Intent)} 后调用 <br>
     * 使用 {@link #getLastCustomNonConfigurationInstance()}
     * @param data
     */
    protected abstract void onRestoreData(Map<String, Object> data);

    /**
     * 调用 {@link #onInitData(Bundle, Intent)} 后调用
     * @param savedInstanceState
     */
    protected abstract void onInitView(@Nullable Bundle savedInstanceState);

    /**
     * 懒加载模式, {@link #onPostCreate(Bundle)} 后调用
     */
    protected abstract void onLazyLoad();

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
    protected <T extends TitleBar>T getTitleBar() {
        return (T) mTitleBar;
    }

    /* ============== TitleBar_End ============== */
}
