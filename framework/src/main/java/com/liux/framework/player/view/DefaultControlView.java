package com.liux.framework.player.view;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.liux.framework.R;
import com.liux.framework.player.Player;

/**
 * 默认的播放控制器
 * Created by Liux on 2017/9/18.
 */

public class DefaultControlView extends FrameLayout implements ControlView {

    private PlayerView mPlayerView;

    private View mRoot;
    // 标题栏元素
    private View mBackRoot;
    private ImageView mBack;
    private TextView mTitle;
    // 控制栏元素
    private ImageView mPlayLittle;
    private TextView mTimeNow;
    private SeekBar mSeekBar;
    private TextView mTimeAll;
    private ImageView mFullScreen;
    // 播放按钮
    private ImageView mPlay;
    // 等待
    private View mWaitRoot;
    private TextView mWaitText;
    // 亮度/音量调整
    private View mAdjustRoot;
    private ImageView mAdjustIcon;
    private TextView mAdjustText;
    // 提醒
    private ImageView mWarning;
    // 误触锁
    private ImageView mLock;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (v == getView()) {
                showControlView();
            } else if (id == R.id.player_ll_back) {

            } else if (id == R.id.player_iv_play_little) {

            } else if (id == R.id.player_iv_fullscreen) {
                toggleFullScreen();
            } else if (id == R.id.player_iv_play) {

            } else if (id == R.id.player_iv_lock) {

            }
        }
    };

    private SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return false;
        }
    };

    public DefaultControlView(PlayerView view) {
        super(view.getView().getContext());
        mPlayerView = view;

        initView();
    }

    @Override
    public View getView() {
        return this;
    }

    private void initView() {
        mRoot = LayoutInflater.from(getContext()).inflate(R.layout.view_player_control_default, this, false);

        mBackRoot = mRoot.findViewById(R.id.player_ll_back);
        mBack = (ImageView) mRoot.findViewById(R.id.player_iv_back);
        mTitle = (TextView) mRoot.findViewById(R.id.player_tv_title);

        mPlayLittle = (ImageView) mRoot.findViewById(R.id.player_iv_play_little);
        mTimeNow = (TextView) mRoot.findViewById(R.id.player_tv_time_now);
        mSeekBar = (SeekBar) mRoot.findViewById(R.id.player_sb_seekbar);
        mTimeAll = (TextView) mRoot.findViewById(R.id.player_tv_time_all);
        mFullScreen = (ImageView) mRoot.findViewById(R.id.player_iv_fullscreen);

        mPlay = (ImageView) mRoot.findViewById(R.id.player_iv_play);

        mWaitRoot = mRoot.findViewById(R.id.player_ll_wait);
        mWaitText = (TextView) mRoot.findViewById(R.id.player_tv_adjust_text);

        mAdjustRoot = mRoot.findViewById(R.id.player_ll_adjust);
        mAdjustIcon = (ImageView) mRoot.findViewById(R.id.player_iv_adjust_icon);
        mAdjustText = (TextView) mRoot.findViewById(R.id.player_tv_adjust_text);

        mWarning = (ImageView) mRoot.findViewById(R.id.player_iv_warning);

        mLock = (ImageView) mRoot.findViewById(R.id.player_iv_lock);

        setOnClickListener(mOnClickListener);
        mBackRoot.setOnClickListener(mOnClickListener);
        mPlayLittle.setOnClickListener(mOnClickListener);
        mFullScreen.setOnClickListener(mOnClickListener);
        mPlay.setOnClickListener(mOnClickListener);
        mLock.setOnClickListener(mOnClickListener);

        mSeekBar.setOnSeekBarChangeListener(mOnSeekBarChangeListener);

        setOnTouchListener(mOnTouchListener);

        addView(mRoot);
    }

    private void showControlView() {
        if (mRoot == null) return;

        mRoot.setVisibility(View.VISIBLE);

        mRoot.postDelayed(new Runnable() {
            @Override
            public void run() {
                mRoot.setVisibility(View.GONE);
            }
        }, 5 * 1000);
    }

    private void toggleFullScreen() {
        if (mPlayerView.getFullScreen()) {
            mPlayerView.setFullScreen(false);
            mFullScreen.setImageResource(R.drawable.ic_player_fullscreen_close);
        } else {
            mPlayerView.setFullScreen(true);
            mFullScreen.setImageResource(R.drawable.ic_player_fullscreen_open);
        }
    }
}
