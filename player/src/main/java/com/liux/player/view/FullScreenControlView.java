package com.liux.player.view;

import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.view.LayoutInflater;
import android.view.View;

import com.liux.player.R;
import com.liux.player.util.GestureHelper;

/**
 * Created by Liux on 2017/11/26.
 */

public class FullScreenControlView extends AbstractControlView {
    private static final int TYPE_SEEK = 1;
    private static final int TYPE_VOLUME = 2;
    private static final int TYPE_BRIGHTNESS = 3;

    private AbstractPlayerView mAbstractPlayerView;

    private View mRoot;
//    // 标题栏元素
//    private View mTitleRoot;
//    private View mBackRoot;
//    private ImageView mBack;
//    private TextView mTitle;
//    // 播放栏元素
//    private View mPlayRoot;
//    private ImageView mPlayLittle;
//    private TextView mTimeNow;
//    private SeekBar mSeekBar;
//    private TextView mTimeAll;
//    private ImageView mFullScreen;
//    // 播放按钮
//    private ImageView mPlay;
//    // 等待
//    private View mWaitRoot;
//    private TextView mWaitText;
//    // 亮度/音量调整
//    private View mAdjustRoot;
//    private ImageView mAdjustIcon;
//    private TextView mAdjustText;
//    // 提醒
//    private ImageView mWarning;
//    // 误触锁
//    private ImageView mLock;

    private AudioManager mAudioManager;

    // 误触锁状态
    private boolean mLockState = false;
    // 记录全屏之前屏幕旋转参数
    private int mOriginalOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;

//    private OnClickListener mOnClickListener = new OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            delayMainView();
//            int id = v.getId();
//            if (v == DefaultControlView.this) {
//                toggleControl();
//            } else if (id == R.id.player_ll_back) {
//                toggleFullScreen();
//            } else if (id == R.id.player_iv_play_little) {
//                togglePlay();
//            } else if (id == R.id.player_iv_fullscreen) {
//                toggleFullScreen();
//            } else if (id == R.id.player_iv_play) {
//
//            } else if (id == R.id.player_iv_lock) {
//                toggleLock();
//            }
//        }
//    };
//
//    private SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
//        private int mLastPosition = 0;
//        @Override
//        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//            double pos = seekBar.getProgress() / 10000.0 * mPlayerView.getDuration();
//            mTimeNow.setText(ViewUtil.parseTime((int) pos));
//            delayMainView();
//        }
//
//        @Override
//        public void onStartTrackingTouch(SeekBar seekBar) {
//            mSeekState = true;
//            mTimeNow.setText(ViewUtil.parseTime(mPlayerView.getCurrentPosition()));
//            mLastPosition = seekBar.getProgress();
//            delayMainView();
//        }
//
//        @Override
//        public void onStopTrackingTouch(SeekBar seekBar) {
//            mSeekState = false;
//            mTimeNow.setText(ViewUtil.parseTime(mPlayerView.getCurrentPosition()));
//            double pos = seekBar.getProgress() / 10000.0 * mPlayerView.getDuration();
//
//            if (mPlayerView.canSeekBackward() && mPlayerView.canSeekForward()) {
//                mPlayerView.seekTo((int) pos);
//            } else {
//                seekBar.setProgress(mLastPosition);
//            }
//            delayMainView();
//        }
//    };
//
//    private GestureHelper.OnGestureListener mOnGestureListener = new GestureHelper.OnGestureListener() {
//
//        @Override
//        public void onClick(View view) {
//            mOnClickListener.onClick(view);
//        }
//
//        @Override
//        public boolean canDrag() {
//            return !mLockState && mPlayerView.isFullScreen();
//        }
//
//        @Override
//        public boolean canSeek() {
//            return mPlayerView.canSeekBackward() && mPlayerView.canSeekForward();
//        }
//
//        @Override
//        public float getSeekRange() {
//            return (5 * 60 * 1000.0f) / mPlayerView.getDuration();
//        }
//
//        @Override
//        public float getSeekRatio() {
//            return (mPlayerView.getCurrentPosition() + 0.0f) / mPlayerView.getDuration();
//        }
//
//        @Override
//        public void setSeekRatio(float second, boolean set) {
//            int pos = (int) (second * mPlayerView.getDuration());
//            refreshSeek(pos);
//
//            if (!set) return;
//            mPlayerView.seekTo(pos);
//        }
//
//        @Override
//        public float getVolumeRatio() {
//            return (mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) + 0.0f) /
//                    mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
//        }
//
//        @Override
//        public void setVolumeRatio(float volume) {
//            refreshVolume((int) (100 * volume));
//
//            int vol = (int) (volume * mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
//            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, vol, AudioManager.FLAG_SHOW_UI);
//        }
//
//        @Override
//        public float getBrightnessRatio() {
//            return ViewUtil.getScreenBrightness(getContext());
//        }
//
//        @Override
//        public void setBrightnessRatio(float brightness) {
//            refreshBrightness((int) (100 * brightness));
//
//            Window window = ((Activity) getContext()).getWindow();
//            WindowManager.LayoutParams layoutParams = window.getAttributes();
//            layoutParams.screenBrightness = brightness;
//            window.setAttributes(layoutParams);
//        }
//    };

    public FullScreenControlView(AbstractPlayerView view) {
        super(view.getContext());

        mAbstractPlayerView = view;
        mRoot = LayoutInflater.from(getContext()).inflate(R.layout.view_player_control_full, this, true);

//        GestureHelper.install(this, mOnGestureListener);
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
