package com.liux.framework.player.view;

import android.content.Context;
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

public class DefaultControlView extends FrameLayout implements ControlView {

    private Player mPlayer;
    private PlayerView mPlayerView;

    private View mRoot;

    public DefaultControlView(Player player, PlayerView view) {
        super(view.getContext());
        mPlayer = player;
        mPlayerView = view;
        initView();
    }

    private void initView() {
        FrameLayout.LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.gravity = Gravity.CENTER;

        mRoot = LayoutInflater.from(getContext()).inflate(R.layout.view_player_control_default, null);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayerView.setFullScreen(!mPlayerView.getFullScreen());
            }
        });

        addView(mRoot, lp);
    }
}
