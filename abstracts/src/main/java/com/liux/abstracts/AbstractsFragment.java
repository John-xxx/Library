package com.liux.abstracts;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Field;

/**
 * 抽象Fragment,提供以下能力 <br>
 * 1.自动隐藏输入法 {@link HandlerTouch} <br>
 * 2.重定义生命周期细节 {@link #onInitData(Bundle)} {@link #onRestoreData(Bundle)} {@link #onInitView(LayoutInflater, ViewGroup, Bundle)} <br>
 *     {@link #onLazyLoad()} {@link #onSaveData(Bundle)} {@link #onVisibleChanged()}
 * 3.修复某些版本某些情况下 Fragent 显示状态不保存的问题
 * Created by Liux on 2017/8/7.
 */

public abstract class AbstractsFragment extends Fragment implements HandlerTouch {
    private String TAG = "AbstractsFragment";

    private static final String STATE_SAVE_IS_HIDDEN = "STATE_SAVE_IS_HIDDEN";

    /* ============== 生命周期_Begin ============== */

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_SAVE_IS_HIDDEN)) {
            boolean isHidden = savedInstanceState.getBoolean(STATE_SAVE_IS_HIDDEN);
            savedInstanceState.remove(STATE_SAVE_IS_HIDDEN);

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            if (isHidden) {
                ft.hide(this);
            } else {
                ft.show(this);
            }
            ft.commit();
        }

        onInitData(getArguments());

        if (savedInstanceState != null && !savedInstanceState.isEmpty()) {
            onRestoreData(savedInstanceState);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return onInitView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewCreated = true;
        checkLazyLoad();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

        checkVisibleChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();

        mStopCalled = true;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(STATE_SAVE_IS_HIDDEN, isHidden());

        onSaveData(outState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        try {
            // http://blog.csdn.net/primer_programer/article/details/27184877
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* ============== 生命周期_End ============== */



    /* ============== 其他回调_Begin ============== */

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (hidden)
            checkVisibleChanged();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        boolean callLazyLoad = mCallLazyLoad;

        mUserVisible = isVisibleToUser;
        checkLazyLoad();

        if (callLazyLoad)
            checkVisibleChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /* ============== 其他回调_End ============== */



    /* ============== 数据和View_Begin ============== */

    /**
     * {@link #onCreate(Bundle)} 后调用 <br>
     * 初始化数据
     * @param savedInstanceState
     */
    protected abstract void onInitData(Bundle savedInstanceState);

    /**
     * {@link #onInitData(Bundle)} 后调用
     * @param data
     */
    protected abstract void onRestoreData(Bundle data);

    /**
     * {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)} 后调用 <br>
     * 初始化布局
     * @param inflater
     * @param container
     * @param savedInstanceState
     */
    protected abstract View onInitView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    /**
     * 懒加载模式,保证创建完成后第一次显示时调用一次 <br>
     * {@link #onViewCreated(View, Bundle)}
     * {@link #setUserVisibleHint(boolean)}
     */
    protected abstract void onLazyLoad();

    /**
     * {@link #onSaveInstanceState(Bundle)} 后调用
     * @param data
     */
    protected abstract void onSaveData(Bundle data);

    /**
     * 每次重新在前台展示时调用,与 {@link #onLazyLoad} 互斥
     * {@link #onStart()} 后调用
     * {@link #onHiddenChanged(boolean)} 后调用
     */
    protected abstract void onVisibleChanged();

    /* ============== 数据和View_End ============== */



    /* ============== 懒加载_Begin ============== */

    private boolean mViewCreated = false;
    private boolean mUserVisible = false;
    private boolean mCallLazyLoad = false;

    /**
     * 检查是否调用懒加载方法
     * 保证在视图创建完成后第一次显示时调用一次目标方法
     */
    private void checkLazyLoad() {
        if (mCallLazyLoad) return;
        if (!mViewCreated) return;
        if (!mUserVisible) return;

        mCallLazyLoad = true;
        onLazyLoad();
    }

    /**
     * 当Fragment为第一个展示的页面时,会调用 {@link #onStart()} 方法
     */
    private boolean mStopCalled = false;

    /**
     * 检查视图状态是否已经改变为可视状态
     * 保证是在懒加载和视图创建完毕之后的生命周期中调用
     */
    private void checkVisibleChanged() {
        if (!mStopCalled) {
            mStopCalled = true;
            return;
        }

        if (!mCallLazyLoad) return;
        if (!mViewCreated) return;
        if (!mUserVisible) return;

        onVisibleChanged();
    }

    /* ============== 懒加载_End ============== */

    /* ========== 拦截点击相关_Begin ========== */

    @Override
    public boolean isHandlerTouch() {
        if (getHandlerTouch() == null) return false;
        if (!getHandlerTouch().isHandlerTouch()) return false;
        if (getHandlerTouch().hasIgnoreView(getView())) return false;
        return true;
    }

    /**
     * 设置Fragment不处理触控事件的原理是添加Fragment的View进过滤规则内
     * @param handlerTouch
     */
    @Override
    public void setHandlerTouch(boolean handlerTouch) {
        if (getHandlerTouch() == null) return;
        if (!getHandlerTouch().isHandlerTouch()) return;
        if (handlerTouch) {
            getHandlerTouch().removeIgnoreView(getView());
        } else {
            getHandlerTouch().addIgnoreView(getView());
        }
    }

    @Override
    public boolean hasIgnoreView(View view) {
        if (getHandlerTouch() == null) return false;
        return getHandlerTouch().hasIgnoreView(view);
    }

    @Override
    public void addIgnoreView(View view) {
        if (getHandlerTouch() == null) return;
        getHandlerTouch().addIgnoreView(view);
    }

    @Override
    public void removeIgnoreView(View view) {
        if (getHandlerTouch() == null) return;
        getHandlerTouch().removeIgnoreView(view);
    }

    private HandlerTouch getHandlerTouch() {
        Activity activity = getActivity();
        if (activity instanceof HandlerTouch) {
            return (HandlerTouch) activity;
        }
        return null;
    }

    /* ========== 拦截点击相关_End ========== */
}
