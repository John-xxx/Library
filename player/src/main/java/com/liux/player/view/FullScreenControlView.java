package com.liux.player.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.liux.player.Media;
import com.liux.player.R;
import com.liux.player.util.GestureHelper;
import com.liux.player.util.ViewUtil;

/**
 * Created by Liux on 2017/11/26.
 */

public class FullScreenControlView extends AbstractControlView {
    private static final int MSG_AUTO_HIDE = 1;
    private static final int MSG_REFRESH_TIME = 3;

    private static final int TIME_AUTO_HIDE = 5 * 1000;

    private static final int VIEW_HINT_PLAY = 1;
    private static final int VIEW_HINT_WAIT = 2;
    private static final int VIEW_HINT_WARN = 3;
    private static final int VIEW_HINT_NUM_LIGHT = 4;
    private static final int VIEW_HINT_NUM_SOUND = 5;
    private static final int VIEW_HINT_NUM_SEEK = 6;

    private AbstractPlayerView mAbstractPlayerView;

    private View mRoot;
    // 误触锁
    private ImageView mLock;
    // 标题栏元素
    private View mTitleRoot;
    private View mBackRoot;
    private ImageView mBack;
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
    private View mWaitRoot;
    private TextView mWaitText;
    // 亮度/音量调整
    private View mAdjustRoot;
    private ImageView mAdjustIcon;
    private TextView mAdjustText;
    // 提醒
    private ImageView mWarn;

    private AudioManager mAudioManager;

    // 误触锁状态
    private boolean mLockState = false;
    private boolean mSeekState = false;
    private boolean mIsPrepared = false;
    // 记录全屏之前屏幕旋转参数
    private int mOriginalOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_AUTO_HIDE:
                    hideMainView();
                    hideHintView();
                    break;
                case MSG_REFRESH_TIME:
                    if (!mSeekState) {
                        mTimeNow.setText(ViewUtil.parseTime(mAbstractPlayerView.getCurrentPosition()));
                    }
                    if (mAbstractPlayerView.getDuration() != 0) {
                        mSeekBar.setSecondaryProgress(mAbstractPlayerView.getBufferPercentage() * mAbstractPlayerView.getDuration() / 100);
                        if (!mSeekState) {
                            mSeekBar.setProgress(mAbstractPlayerView.getCurrentPosition());
                        }
                    } else {
                        mSeekBar.setSecondaryProgress(0);
                        if (!mSeekState) {
                            mSeekBar.setProgress(0);
                        }
                    }
                    sendEmptyMessageDelayed(MSG_REFRESH_TIME, 200);
                    break;
            }
        }
    };

    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.player_iv_lock) {
                if (!mLockState) {
                    mLockState = true;
                    mLock.setImageResource(R.drawable.ic_player_lock_close);
                    hideHintView();
                    hideMainView();
                } else {
                    mLockState = false;
                    mLock.setImageResource(R.drawable.ic_player_lock_open);
                }
            } else if (id == R.id.player_ll_back) {
                mAbstractPlayerView.closeFullScreen();
            } else if (id == R.id.player_iv_play_little || id == R.id.player_iv_play) {
                if (!mAbstractPlayerView.isPlaying()) {
                    mAbstractPlayerView.start();
                } else {
                    mAbstractPlayerView.pause();
                }
                delayHideTime();
            } else if (id == R.id.player_iv_fullscreen) {
                mAbstractPlayerView.closeFullScreen();
                delayHideTime();
            } else  {
                if (!mIsPrepared) return;
                if (isShowHintView() || isShowMainView()) {
                    hideHintView();
                    hideMainView();
                    return;
                } else {
                    showMainView();
                    showHintView(VIEW_HINT_PLAY);
                    delayHideTime();
                }
            }
        }
    };

    private GestureHelper.OnGestureListener mOnGestureListener = new GestureHelper.OnGestureListener() {

        @Override
        public void onClick(View view) {
            mOnClickListener.onClick(view);
        }

        @Override
        public boolean canDrag() {
            return !mLockState;
        }

        @Override
        public boolean canSeek() {
            return mAbstractPlayerView.canSeekBackward() && mAbstractPlayerView.canSeekForward();
        }

        @Override
        public float getSeekRange() {
            return (5 * 60 * 1000.0f) / mAbstractPlayerView.getDuration();
        }

        @Override
        public float getSeekRatio() {
            return (mAbstractPlayerView.getCurrentPosition() + 0.0f) / mAbstractPlayerView.getDuration();
        }

        @Override
        public void setSeekRatio(float second, boolean set) {
            int pos = (int) (second * mAbstractPlayerView.getDuration());
            int diff = pos - mAbstractPlayerView.getCurrentPosition();
            mAdjustIcon.setImageResource(diff > 0 ? R.drawable.ic_player_forward : R.drawable.ic_player_backward);
            mAdjustText.setText(ViewUtil.parseTime(pos));
            showHintView(VIEW_HINT_NUM_SEEK);

            if (!set) return;
            mAbstractPlayerView.seekTo(pos);
        }

        @Override
        public float getVolumeRatio() {
            return (mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) + 0.0f) /
                    mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        }

        @Override
        public void setVolumeRatio(float volume) {
            mAdjustIcon.setImageResource(volume != 0 ? R.drawable.ic_player_sound : R.drawable.ic_player_sound_off);
            mAdjustText.setText(((int) (100 * volume)) + "%");
            showHintView(VIEW_HINT_NUM_SOUND);

            int vol = (int) (volume * mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, vol, AudioManager.FLAG_SHOW_UI);
        }

        @Override
        public float getBrightnessRatio() {
            return ViewUtil.getScreenBrightness(getContext());
        }

        @Override
        public void setBrightnessRatio(float brightness) {
            mAdjustIcon.setImageResource(R.drawable.ic_player_light);
            mAdjustText.setText(((int) (100 * brightness)) + "%");
            showHintView(VIEW_HINT_NUM_SOUND);

            Window window = ((Activity) getContext()).getWindow();
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.screenBrightness = brightness;
            window.setAttributes(layoutParams);
        }
    };

    public FullScreenControlView(AbstractPlayerView view) {
        super(view.getContext());

        mAbstractPlayerView = view;
        mRoot = LayoutInflater.from(getContext()).inflate(R.layout.view_player_control_full, this, true);

        mLock = (ImageView) mRoot.findViewById(R.id.player_iv_lock);

        mTitleRoot = mRoot.findViewById(R.id.player_ll_title);
        mBackRoot = mRoot.findViewById(R.id.player_ll_back);
        mBack = (ImageView) mRoot.findViewById(R.id.player_iv_back);
        mTitle = (TextView) mRoot.findViewById(R.id.player_tv_title);

        mPlayRoot = mRoot.findViewById(R.id.player_ll_play);
        mPlayLittle = (ImageView) mRoot.findViewById(R.id.player_iv_play_little);
        mTimeNow = (TextView) mRoot.findViewById(R.id.player_tv_time_now);
        mSeekBar = (SeekBar) mRoot.findViewById(R.id.player_sb_seekbar);
        mTimeAll = (TextView) mRoot.findViewById(R.id.player_tv_time_all);
        mFullScreen = (ImageView) mRoot.findViewById(R.id.player_iv_fullscreen);

        mPlay = (ImageView) mRoot.findViewById(R.id.player_iv_play);

        mWaitRoot = mRoot.findViewById(R.id.player_ll_wait);
        mWaitText = (TextView) mRoot.findViewById(R.id.player_tv_wait_text);

        mAdjustRoot = mRoot.findViewById(R.id.player_ll_adjust);
        mAdjustIcon = (ImageView) mRoot.findViewById(R.id.player_iv_adjust_icon);
        mAdjustText = (TextView) mRoot.findViewById(R.id.player_tv_adjust_text);

        mWarn = (ImageView) mRoot.findViewById(R.id.player_iv_warning);

        setOnClickListener(mOnClickListener);
        mRoot.setOnClickListener(mOnClickListener);
        mPlayLittle.setOnClickListener(mOnClickListener);
        mFullScreen.setOnClickListener(mOnClickListener);
        mPlay.setOnClickListener(mOnClickListener);

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mTimeNow.setText(ViewUtil.parseTime(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mSeekState = true;
                cancelHideTime();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mSeekState = false;
                if (mAbstractPlayerView.canSeekBackward() && mAbstractPlayerView.canSeekForward()) {
                    mAbstractPlayerView.seekTo(seekBar.getProgress());
                } else {
                    mSeekBar.setProgress(0);
                }
            }
        });

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mIsPrepared
                        && mAbstractPlayerView.isPlaying()
                        && (isShowMainView() || isShowHintView())) {
                    delayHideTime();
                }
                return false;
            }
        });

        GestureHelper.install(this, mOnGestureListener);
    }

    @Override
    public void loadMedia() {
        resetUI();
    }

    @Override
    public void prepare() {

    }

    @Override
    public void prepared() {

    }

    @Override
    public void error(int error) {
        showHintView(VIEW_HINT_WARN);
        cancelHideTime();
    }

    @Override
    public void playStart() {
        mIsPrepared = true;
        hideMainView();
        hideHintView();
    }

    @Override
    public void playComplete() {
        resetUI();
    }

    @Override
    public void changeFocus() {
        resetUI();
    }

    @Override
    public void changeScreen() {

    }

    @Override
    public void callStart() {
        mPlayLittle.setImageResource(R.drawable.ic_player_pause_little);
        mPlay.setImageResource(R.drawable.ic_player_pause);
    }

    @Override
    public void callPause() {
        mPlayLittle.setImageResource(R.drawable.ic_player_play_little);
        mPlay.setImageResource(R.drawable.ic_player_play);
    }

    @Override
    public void callStop() {
        resetUI();
    }

    @Override
    public void callReset() {
        resetUI();
    }

    @Override
    public void callRelease() {
        resetUI();
    }

    @Override
    public void bufferStart() {
        showHintView(VIEW_HINT_WAIT);
        cancelHideTime();
    }

    @Override
    public void bufferEnd() {
        hideHintView();
        delayHideTime();
    }

    @Override
    public void bufferUpdate(int percent) {

    }

    @Override
    public void seekStart() {
        showHintView(VIEW_HINT_WAIT);
        cancelHideTime();
    }

    @Override
    public void seekEnd() {
        hideHintView();
        delayHideTime();
    }

    /**
     * 重置UI
     */
    private void resetUI() {
        mIsPrepared = false;
        cancelHideTime();
        hideMainView();
        showHintView(VIEW_HINT_PLAY);
        mPlayLittle.setImageResource(R.drawable.ic_player_play_little);
        mPlay.setImageResource(R.drawable.ic_player_play);
    }

    /**
     * 启动/延长定时器
     */
    private void delayHideTime() {
        cancelHideTime();
        mHandler.sendEmptyMessageDelayed(MSG_AUTO_HIDE, TIME_AUTO_HIDE);
    }

    /**
     * 取消定时器
     */
    private void cancelHideTime() {
        mHandler.removeMessages(MSG_AUTO_HIDE);
    }

    private boolean isShowMainView() {
        return mLock.getVisibility() == VISIBLE
                || mTitle.getVisibility() == VISIBLE
                || mPlayRoot.getVisibility() == VISIBLE;
    }

    /**
     * 显示主UI
     */
    private void showMainView() {
        if (!mIsPrepared) return;

        if (mLockState) {
            mLock.setVisibility(VISIBLE);
            return;
        }

        mLock.setVisibility(VISIBLE);

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
        if (duration != 0) {
            mSeekBar.setMax(duration);
            mSeekBar.setSecondaryProgress(mAbstractPlayerView.getBufferPercentage() * mAbstractPlayerView.getDuration() / 100);
            mSeekBar.setProgress(mAbstractPlayerView.getCurrentPosition());
            mTimeAll.setText(ViewUtil.parseTime(duration));
        } else {
            mSeekBar.setMax(10000);
            mSeekBar.setSecondaryProgress(0);
            mSeekBar.setProgress(0);
            mTimeAll.setText("00∶00");
        }

        mTitle.setVisibility(VISIBLE);
        mPlayRoot.setVisibility(VISIBLE);
        mHandler.sendEmptyMessage(MSG_REFRESH_TIME);
    }

    /**
     * 隐藏主UI
     */
    private void hideMainView() {
        mTitle.setVisibility(GONE);
        mPlayRoot.setVisibility(GONE);
        mLock.setVisibility(GONE);
        mHandler.removeMessages(MSG_REFRESH_TIME);
    }

    private boolean isShowHintView() {
        return mPlay.getVisibility() == VISIBLE
                || mWaitRoot.getVisibility() == VISIBLE
                || mWarn.getVisibility() == VISIBLE
                || mAdjustRoot.getVisibility() == VISIBLE;
    }

    /**
     * 显示某个提示性UI
     * @param type
     */
    private void showHintView(int type) {
        hideHintView();
        switch (type) {
            case VIEW_HINT_PLAY:
                if (mLockState) break;
                mPlay.setVisibility(VISIBLE);
                break;
            case VIEW_HINT_WAIT:
                mWaitRoot.setVisibility(VISIBLE);
                break;
            case VIEW_HINT_WARN:
                mWarn.setVisibility(VISIBLE);
                break;
            case VIEW_HINT_NUM_LIGHT:
                mAdjustRoot.setVisibility(VISIBLE);
                break;
            case VIEW_HINT_NUM_SOUND:
                mAdjustRoot.setVisibility(VISIBLE);
                break;
            case VIEW_HINT_NUM_SEEK:
                mAdjustRoot.setVisibility(VISIBLE);
                break;
        }
    }

    /**
     * 隐藏提示性UI
     */
    private void hideHintView() {
        mPlay.setVisibility(GONE);
        mWaitRoot.setVisibility(GONE);
        mWarn.setVisibility(GONE);
        mAdjustRoot.setVisibility(GONE);
    }
}
