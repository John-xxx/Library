package com.liux.framework.player.view;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.liux.framework.R;
import com.liux.framework.player.Player;

/**
 * 默认的播放控制器
 * Created by Liux on 2017/9/18.
 */

public class DefaultControlView implements ControlView {

    private PlayerView mPlayerView;

    private View mRoot;

    public DefaultControlView(PlayerView view) {
        mPlayerView = view;

        initView();
    }

    @Override
    public View getView() {
        return mRoot;
    }

    private void initView() {
        AbstractPlayerView playerView = (AbstractPlayerView) mPlayerView;
        mRoot = LayoutInflater.from(playerView.getContext()).inflate(R.layout.view_player_control_default, null);
    }
}
