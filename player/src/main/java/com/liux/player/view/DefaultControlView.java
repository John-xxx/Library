package com.liux.player.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.liux.player.Media;
import com.liux.player.R;
import com.liux.player.PlayerView;
import com.liux.player.util.GestureHelper;
import com.liux.player.util.ViewUtil;

import java.util.Locale;

/**
 * 默认的播放控制器
 * Created by Liux on 2017/9/18.
 */

@SuppressLint("ViewConstructor")
public class DefaultControlView extends AbstractControlView {
    private static final int MSG_AUTO_HIDE = 1;
    private static final int MSG_REFRESH_TIME = 3;
    private static final int TIME_AUTO_HIDE = 5 * 1000;

    private AbstractPlayerView mAbstractPlayerView;

    private View mRoot;
    // 标题栏元素
    private TextView mTitle;
    // 播放栏元素
    private View mPlayRoot;
    private ImageView mPlayLittle;
    private TextView mTimeNow;
    private SeekBar mSeekBar;
    private TextView mTimeAll;
    private ImageView mFullScreen;
    // 播放按钮
    private ImageView mPlay;
    // 等待
    private View mWait;
    // 提醒
    private ImageView mWarn;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_AUTO_HIDE:
                    hideControlView();
                    break;
                case MSG_REFRESH_TIME:
                    mTimeNow.setText(ViewUtil.parseTime(mAbstractPlayerView.getCurrentPosition()));
                    sendEmptyMessageDelayed(MSG_REFRESH_TIME, 200);
                    break;
            }
        }
    };

    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int i = v.getId();
            if (i == R.id.player_iv_play_little || i == R.id.player_iv_play) {
                if (!mAbstractPlayerView.isPlaying()) {
                    mAbstractPlayerView.start();
                } else {
                    mAbstractPlayerView.pause();
                }
            } else if (i == R.id.player_iv_fullscreen) {
                if (!mAbstractPlayerView.isFullScreen()) {
                    mAbstractPlayerView.openFullScreen();
                } else {
                    // mAbstractPlayerView.closeSmallScreen();
                }
            } else {
                if (mRoot.getVisibility() == VISIBLE) {
                    hideControlView();
                    return;
                } else {
                    showControlView();
                }
            }
            delayHideTime();
        }
    };

    private void cancelHide() {
        mHandler.removeMessages(MSG_AUTO_HIDE);
    }

    private void delayHideTime() {
        cancelHide();
        mHandler.sendEmptyMessageDelayed(MSG_AUTO_HIDE, TIME_AUTO_HIDE);
    }

    private void hideControlView() {
        cancelHide();
        mRoot.setVisibility(GONE);
    }

    private void showControlView() {
        delayHideTime();

        String title = null;
        if (!Media.isEmpty(mAbstractPlayerView.getMedia())) {
            title = mAbstractPlayerView.getMedia().getTitle();
            if (TextUtils.isEmpty(title)) {
                title = mAbstractPlayerView.getMedia().getUri().toString();
            }
        }
        mTitle.setText(title);

        mPlayLittle.setImageResource(mAbstractPlayerView.isPlaying() ? R.drawable.ic_player_pause_little : R.drawable.ic_player_play_little);
        mTimeNow.setText(ViewUtil.parseTime(mAbstractPlayerView.getCurrentPosition()));

        int duration = mAbstractPlayerView.getDuration();
        if (duration == 0) {
            mSeekBar.setProgress(0);
            mTimeAll.setText("00∶00");
        } else {
            mSeekBar.setMax(duration);
            mSeekBar.setProgress(mAbstractPlayerView.getCurrentPosition());
            mTimeAll.setText(ViewUtil.parseTime(duration));
        }

        mRoot.setVisibility(VISIBLE);
        mHandler.sendEmptyMessage(MSG_REFRESH_TIME);
    }

    public DefaultControlView(AbstractPlayerView view) {
        super(view.getContext());

        mAbstractPlayerView = view;
        mRoot = LayoutInflater.from(getContext()).inflate(R.layout.view_player_control_default, this, true);

        mTitle = (TextView) mRoot.findViewById(R.id.player_tv_title);

        mPlayRoot = mRoot.findViewById(R.id.player_ll_play);
        mPlayLittle = (ImageView) mRoot.findViewById(R.id.player_iv_play_little);
        mTimeNow = (TextView) mRoot.findViewById(R.id.player_tv_time_now);
        mSeekBar = (SeekBar) mRoot.findViewById(R.id.player_sb_seekbar);
        mTimeAll = (TextView) mRoot.findViewById(R.id.player_tv_time_all);
        mFullScreen = (ImageView) mRoot.findViewById(R.id.player_iv_fullscreen);

        mPlay = (ImageView) mRoot.findViewById(R.id.player_iv_play);

        mWait = mRoot.findViewById(R.id.player_pb_wait);

        mWarn = (ImageView) mRoot.findViewById(R.id.player_iv_warning);

        setOnClickListener(mOnClickListener);
        mRoot.setOnClickListener(mOnClickListener);
        mPlayLittle.setOnClickListener(mOnClickListener);
        mFullScreen.setOnClickListener(mOnClickListener);
        mPlay.setOnClickListener(mOnClickListener);

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                delayHideTime();
                return false;
            }
        });
    }

    @Override
    public void loadMedia() {

    }

    @Override
    public void prepare() {

    }

    @Override
    public void prepared() {

    }

    @Override
    public void error(int error) {

    }

    @Override
    public void playStart() {

    }

    @Override
    public void playComplete() {

    }

    @Override
    public void changeFocus() {

    }

    @Override
    public void changeScreen() {

    }

    @Override
    public void callStart() {

    }

    @Override
    public void callPause() {

    }

    @Override
    public void callStop() {

    }

    @Override
    public void callReset() {

    }

    @Override
    public void callRelease() {

    }

    @Override
    public void bufferStart() {

    }

    @Override
    public void bufferEnd() {

    }

    @Override
    public void bufferUpdate(int percent) {

    }

    @Override
    public void seekStart() {

    }

    @Override
    public void seekEnd() {

    }
}
