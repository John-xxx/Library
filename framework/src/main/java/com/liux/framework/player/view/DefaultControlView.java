package com.liux.framework.player.view;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
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
import com.liux.framework.player.Media;
import com.liux.framework.player.Player;

import java.util.Locale;

/**
 * 默认的播放控制器
 * Created by Liux on 2017/9/18.
 */

public class DefaultControlView extends FrameLayout implements ControlView {
    private static final int TYPE_VOLUME = 1;
    private static final int TYPE_RAY = 2;

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

    private boolean mLockState = false;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

        }
    };

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (v == getView()) {
                showControlView();
            } else if (id == R.id.player_ll_back) {

            } else if (id == R.id.player_iv_play_little) {
                togglePlay();
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

        mRoot.setVisibility(VISIBLE);

        mBack.setVisibility(mPlayerView.getFullScreen() ? VISIBLE : GONE);
        String title = null;
        Media media = mPlayerView.getMedia();
        if (media != null) {
            if (!TextUtils.isEmpty(media.getTitle())) {
                title = media.getTitle();
            } else if (media.getUri() != null) {
                title = media.getUri().getPath();
            }
        }
        mTitle.setText(title);

        mPlayLittle.setImageResource(
                mPlayerView.getPlayer().playing() ? R.drawable.ic_player_pause_little : R.drawable.ic_player_play_little
        );
        mTimeNow.setText(null);
        mSeekBar.setMax(1000);
        mSeekBar.setProgress(400);
        mSeekBar.setSecondaryProgress(500);
        mTimeAll.setText(null);
        mFullScreen.setImageResource(
                mPlayerView.getFullScreen() ? R.drawable.ic_player_fullscreen_close : R.drawable.ic_player_fullscreen_open
        );
    }

    private void showPlay() {
        mPlay.setImageResource(
                mPlayerView.getPlayer().playing() ? R.drawable.ic_player_pause : R.drawable.ic_player_play
        );
        mPlay.setVisibility(VISIBLE);
    }

    private void showWait(int centage) {
        mWaitText.setText(String.format(Locale.CHINA, "%d%", centage));
        mWaitRoot.setVisibility(VISIBLE);
    }

    private void showPlay(int type, int centage) {
        int res = 0;
        String text = null;
        switch (type) {
            case TYPE_VOLUME:
                res = centage != 0 ? R.drawable.ic_player_sound : R.drawable.ic_player_sound_no;
                text = centage != 0 ? String.format(Locale.CHINA, "%d%", centage) : "静音";
                break;
            case TYPE_RAY:
                res = R.drawable.ic_player_light;
                text = String.format(Locale.CHINA, "%d%", centage);
                break;
        }
        mAdjustIcon.setImageResource(res);
        mAdjustText.setText(text);
        mAdjustRoot.setVisibility(VISIBLE);
    }

    private void togglePlay() {
        if (mPlayerView.getPlayer().playing()) {
            mPlayerView.getPlayer().pause();
        } else {
            mPlayerView.getPlayer().start();
        }
    }

    private void toggleFullScreen() {
        if (mPlayerView.getFullScreen()) {
            mPlayerView.setFullScreen(false);
        } else {
            mPlayerView.setFullScreen(true);
        }
    }
}
