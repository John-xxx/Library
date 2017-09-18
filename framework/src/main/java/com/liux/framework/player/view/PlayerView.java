package com.liux.framework.player.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.widget.FrameLayout;

import com.liux.framework.player.Player;
import com.liux.framework.player.PlayerController;

/**
 * Created by Liux on 2017/9/17.
 */

public abstract class PlayerView extends FrameLayout implements RenderView {

    // 对应的视频播放控制器
    private Player mPlayer;

    // 渲染视图
    private View mRender;
    // 控制视图
    private View mControl;
    // 渲染视图控制
    private RenderView mRenderView;
    // 控制视图控制
    private ControlView mControlView;

    public PlayerView(@NonNull Context context) {
        super(context);

        init();
    }

    public PlayerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public PlayerView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PlayerView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        mPlayer.release();
    }

    /**
     * 获取播放控制器
     * @return
     */
    public Player getPlayer() {
        return mPlayer;
    }

    /**
     * 设置自定义的播放控制器
     * 必须实现 {@link ControlView}
     * @param view
     */
    public void setControlView(View view) {
        if (view == null) throw new NullPointerException("ControlView can't be empty");
        if (!(view instanceof ControlView)) throw new ClassCastException("ControlView has to be implements from ControlView");

        int index = indexOfChild(mControl);
        removeView(mControl);

        FrameLayout.LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.gravity = Gravity.CENTER;

        mControl = view;
        mControlView = (ControlView) view;
        getPlayer().setControlView(mControlView);
        addView(mControl, index, lp);
    }

    private void init() {
        mPlayer = initPlayer();
        mPlayer.setPlayerView(this);

        FrameLayout.LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.gravity = Gravity.CENTER;

        mRender = initRenderView();
        mRenderView = this;
        getPlayer().setRenderView(mRenderView);
        addView(mRender, lp);

        mControl = initControlView();
        mControlView = (ControlView) mControl;
        getPlayer().setControlView(mControlView);
        addView(mControl, lp);
    }

    /**
     * 初始化播放控制器
     * @return
     */
    private Player initPlayer() {
        return new PlayerController(getContext());
    }

    /**
     * 初始化渲染 View
     * @return
     */
    protected abstract View initRenderView();

    /**
     * 初始化默认的播放控制器
     * @return
     */
    private View initControlView() {
        return new DefaultControlView(getPlayer(), PlayerView.this);
    }



    // 当前视图根布局
    private ViewGroup mParent;
    // 全屏视图根布局
    private ViewGroup mFullParent;

    /**
     * 获取是否全屏模式
     * @return
     */
    public boolean getFullScreen() {
        return mFullParent != null && getParent() == mFullParent;
    }

    /**
     * 设置是否全屏模式
     * @param fullScreen
     */
    public void setFullScreen(boolean fullScreen) {
        if (getFullScreen() == fullScreen) return;

        if (fullScreen) {
            openFullScreen();
        } else {
            closeFullScreen();
        }

        getPlayer().start();
    }

    /**
     * 开启全屏模式
     */
    private void openFullScreen() {
        if (mParent == null) {
            ViewGroup parent = (ViewGroup) getParent();
            // 新建一个布局替换自己,并在之后当做自己的父布局
            mParent = new FrameLayout(parent.getContext());
            int index = parent.indexOfChild(this);
            parent.removeView(this);
            parent.addView(mParent, index, getLayoutParams());
        } else {
            ViewGroup parent = (ViewGroup) getParent();
            parent.removeView(this);
        }

        if (mFullParent == null) {
            mFullParent = findContentView(mParent);
        }

        FrameLayout.LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        if (mFullParent != null) {
            mFullParent.addView(this, lp);
        } else {
            mParent.addView(this, lp);
        }
    }

    /**
     * 关闭全屏模式
     */
    private void closeFullScreen() {
        if (mParent == null || mFullParent == null) return;

        mFullParent.removeView(this);

        FrameLayout.LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mParent.addView(this, lp);
    }

    /**
     * 从当前布局开始向上查找根内容布局
     * @param view
     * @return
     */
    private ViewGroup findContentView(View view) {
        ViewParent parent = view.getParent();
        while (parent != null && parent instanceof ViewGroup) {
            if (((ViewGroup) parent).getId() == Window.ID_ANDROID_CONTENT) {
                return (ViewGroup) parent;
            } else {
                parent = parent.getParent();
            }
        }
        return null;
    }
}
