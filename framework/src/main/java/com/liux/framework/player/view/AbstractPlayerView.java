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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.liux.framework.R;
import com.liux.framework.player.Player;
import com.liux.framework.player.PlayerController;

/**
 * Created by Liux on 2017/9/17.
 */

public abstract class AbstractPlayerView extends FrameLayout implements PlayerView, ControlView {

    private Player mPlayer;

    private View mRenderView;
    private ViewGroup mControlView;

    public AbstractPlayerView(@NonNull Context context) {
        super(context);

        init();
    }

    public AbstractPlayerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public AbstractPlayerView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AbstractPlayerView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init();
    }

    @Override
    public Player getPlayer() {
        return mPlayer;
    }

    private void init() {
        mPlayer = initPlayer();

        FrameLayout.LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.gravity = Gravity.CENTER;

        mRenderView = initRenderView();
        addView(mRenderView, lp);

        mControlView = initControlView();
        addView(mControlView, lp);
    }

    /**
     * 初始化播放控制器
     * @return
     */
    private Player initPlayer() {
        Player player = new PlayerController(getContext());
        player.setControlView(this);
        return player;
    }

    /**
     * 初始化渲染 View
     * @return
     */
    protected abstract View initRenderView();

    private ViewGroup initControlView() {
        ViewGroup controlView = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.view_player_control, null);

        return controlView;
    }
}
