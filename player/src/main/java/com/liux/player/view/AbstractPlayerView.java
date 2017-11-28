package com.liux.player.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.liux.player.Media;
import com.liux.player.PlayerGroup;
import com.liux.player.PlayerManager;
import com.liux.player.PlayerView;
import com.liux.player.listener.OnBufferListener;
import com.liux.player.listener.OnOperateListener;
import com.liux.player.listener.OnPlayerListener;
import com.liux.player.listener.OnScreenListener;
import com.liux.player.listener.OnSeekListener;
import com.liux.player.listener.OnThumbnailListener;
import com.liux.player.util.FileMediaDataSource;
import com.liux.player.util.Logger;
import com.liux.player.util.PlayerUtil;

import java.io.File;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.IjkTimedText;
import tv.danmaku.ijk.media.player.misc.IMediaDataSource;

/**
 * 播放界面总成(内核+显示+逻辑+其他)
 * Created by Liux on 2017/9/17.
 */

public abstract class AbstractPlayerView extends FrameLayout implements PlayerView {
    private static final String TAG = "AbstractPlayerView";

    /* ========================= Self_Begin ========================= */

    private AbstractRenderView mAbstractRenderView;
    private AbstractThumbView mAbstractThumbView;
    private AbstractControlView mAbstractControlView;

    private boolean mCanSmallScreen = false;
    private PlayerGroup mPlayerGroup;

    private OnPlayerListener mOnPlayerListener;
    private OnThumbnailListener mOnThumbnailListener;
    private OnScreenListener mOnScreenListener;
    private OnOperateListener mOnOperateListener;
    private OnSeekListener mOnSeekListener;
    private OnBufferListener mOnBufferListener;

    private AbstractRenderView.Callback mRenderCallback = new AbstractRenderView.Callback() {
        private boolean mPrepared = false;
        @Override
        public boolean prepared() {
            return  mPrepared;
        }

        @Override
        public void created() {
            mPrepared = true;
            if (mTargetState == STATE_PLAYING) {
                start();
            }
        }

        @Override
        public void destroyed() {
            mPrepared = false;

            // onDetachedFromWindow()
            stop();
            reset();
        }
    };

    private PlayerGroup.Callback mGroupCallback = new PlayerGroup.Callback() {
        @Override
        public void changeFocus(AbstractPlayerView playerView) {
            stop();
            reset();

            mAbstractControlView.changeFocus();

            // 载入缩略图
            mAbstractThumbView.reset();
            mAbstractThumbView.show();
            if (mOnPlayerListener != null && !Media.isEmpty(mMedia)) {
                mOnThumbnailListener.onThumbnail(mAbstractThumbView.getImageView(), mMedia);
            }

            if (mOnScreenListener != null) {
                mOnScreenListener.changeFocus();
            }
        }
    };

    public AbstractPlayerView(@NonNull Context context) {
        super(context);

        initialize();
    }

    public AbstractPlayerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        initialize();
    }

    public AbstractPlayerView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initialize();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AbstractPlayerView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        initialize();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (canSmallScreen()) {
            openSmallScreen();
        }
    }

    @Override
    public void setThumbView(AbstractThumbView thumbView) {
        if (thumbView == null) return;
        if (mAbstractThumbView != thumbView && isLoadedPlayer()) {
            // 改变缩略图布局必须在未载入视频和内核之前
            throw new IllegalStateException("Change the ThumbView must be before the Kernel are loaded");
        }

        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.gravity = Gravity.CENTER;

        int index = indexOfChild(mAbstractThumbView);
        removeView(mAbstractThumbView);
        addView(thumbView, index, lp);
        mAbstractThumbView = thumbView;
    }

    @Override
    public void setControlView(AbstractControlView controlView) {
        if (controlView == null) return;
        if (mAbstractControlView != controlView && isLoadedPlayer()) {
            // 改变缩略图布局必须在未载入视频和内核之前
            throw new IllegalStateException("Change the ControlView must be before the Kernel are loaded");
        }

        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.gravity = Gravity.CENTER;

        int index = indexOfChild(mAbstractControlView);
        removeView(mAbstractControlView);
        addView(controlView, index, lp);
        mAbstractControlView = controlView;
    }

    @Override
    public PlayerGroup getPlayerGroup() {
        return mPlayerGroup;
    }

    @Override
    public void setPlayerGroup(PlayerGroup group) {
        if (mPlayerGroup != group && isLoadedPlayer()) {
            // 改变组播管理器必须在未载入视频和内核之前
            throw new IllegalStateException("Change the PlayerGroup must be before the Kernel are loaded");
        }
        mPlayerGroup = group;
    }

    @Override
    public Decoder getDecoder() {
        return mDecoder;
    }

    @Override
    public void setDecoder(Decoder decoder) {
        if (mDecoder != decoder && isLoadedPlayer()) {
            // 改变解码器必须在未载入视频和内核之前
            throw new IllegalStateException("Change the decoder must be before the Kernel are loaded");
        }
        switch (decoder) {
            case FFMPEG:
            case ANDROID:
            case EXO:
                mDecoder = decoder;
                break;
            default:
                throw new IllegalStateException("Unknown player decoder");
        }
    }

    @Override
    public Media getMedia() {
        return mMedia;
    }

    @Override
    public void setMedia(Media media) {
        Media.checkEmpty(media);

        // 暂停播放并保存播放进度
        if (!media.equals(mMedia)) {
            stop();
            reset();
        }
        mMedia = media;

        // 重置渲染界面
        mAbstractRenderView.clear();

        // 重置控制界面
        mAbstractControlView.loadMedia();

        // 载入缩略图
        mAbstractThumbView.reset();
        mAbstractThumbView.show();
        if (mOnThumbnailListener != null) {
            mOnThumbnailListener.onThumbnail(mAbstractThumbView.getImageView(), mMedia);
        }

        if (mOnPlayerListener != null) {
            mOnPlayerListener.loadMedia();
        }
    }

    @Override
    public boolean isFullScreen() {
        return PlayerManager.checkFullScreen(this);
    }

    @Override
    public void openFullScreen() {
        if (isFullScreen()) return;
        PlayerManager.openFullScreen(this);

        mAbstractControlView.changeScreen();

        if (mOnScreenListener != null) {
            mOnScreenListener.changeScreen();
        }
    }

    @Override
    public void closeFullScreen() {
        if (!isFullScreen()) return;
        PlayerManager.closeFullScreen(this);

        mAbstractControlView.changeScreen();

        if (mOnScreenListener != null) {
            mOnScreenListener.changeScreen();
        }
    }

    @Override
    public boolean canSmallScreen() {
        return mCanSmallScreen;
    }

    @Override
    public void setSmallScreen(boolean canSmallScreen) {
        mCanSmallScreen = canSmallScreen;
    }

    @Override
    public boolean isSmallScreen() {
        return PlayerManager.checkSmallScreen(this);
    }

    @Override
    public void openSmallScreen() {
        if (canSmallScreen() && !isSmallScreen()) {
            PlayerManager.openSmallScreen(this);

            mAbstractControlView.changeScreen();

            if (mOnScreenListener != null) {
                mOnScreenListener.changeScreen();
            }
        }
    }

    @Override
    public void closeSmallScreen() {
        if (!isSmallScreen()) return;
        PlayerManager.closeSmallScreen(this);

        mAbstractControlView.changeScreen();

        if (mOnScreenListener != null) {
            mOnScreenListener.changeScreen();
        }
    }

    @Override
    public void setOnPlayerListener(OnPlayerListener listener) {
        mOnPlayerListener = listener;
    }

    @Override
    public void setOnThumbnailListener(OnThumbnailListener listener) {
        mOnThumbnailListener = listener;
    }

    @Override
    public void setOnScreenListener(OnScreenListener listener) {
        mOnScreenListener = listener;
    }

    @Override
    public void setOnOperateListener(OnOperateListener listener) {
        mOnOperateListener = listener;
    }

    @Override
    public void setOnSeekListener(OnSeekListener listener) {
        mOnSeekListener = listener;
    }

    @Override
    public void setOnBufferListener(OnBufferListener listener) {
        mOnBufferListener = listener;
    }

    @Override
    public boolean onBackPressed() {
        if (isFullScreen()) {
            closeFullScreen();
            return true;
        }
        return false;
    }

    /**
     * 获取渲染视图回调
     * @return
     */
    protected AbstractRenderView.Callback getRenderCallback() {
        return mRenderCallback;
    }

    /**
     * 获取组播视图回调
     * @return
     */
    public PlayerGroup.Callback getGroupCallback() {
        return mGroupCallback;
    }

    /**
     * 初始化
     */
    private void initialize() {
        mContext = getContext().getApplicationContext();

        setBackgroundColor(Color.BLACK);

        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.gravity = Gravity.CENTER;

        mAbstractRenderView = (AbstractRenderView) this;
        addView(mAbstractRenderView.getRenderView(), lp);

        mAbstractThumbView = new DefaultThumbView(this);
        addView(mAbstractThumbView, lp);

        mAbstractControlView = new DefaultControlView(this);
        addView(mAbstractControlView, lp);
    }

    /* ========================= Self_End ========================= */



    /* ========================= Kernel_Begin ========================= */

    // 播放目标
    private Media mMedia;
    // 上下文信息
    private Context mContext;
    // 播放器内核
    private IMediaPlayer mIMediaPlayer;

    private static final int STATE_ERROR = -1;
    private static final int STATE_IDLE = 0;
    private static final int STATE_PREPARING = 1;
    private static final int STATE_PREPARED = 2;
    private static final int STATE_PLAYING = 3;
    private static final int STATE_PAUSED = 4;
    private static final int STATE_PLAYBACK_COMPLETED = 5;

    // 是否能暂停
    private boolean mCanPause = false;
    // 是否能够前进
    private boolean mCanSeekForward = false;
    // 是否能够后退
    private boolean mCanSeekBackward = false;

    // 播放器内核类型
    private Decoder mDecoder = PlayerManager.getDecoder();
    // 当前状态
    private int mCurrentState = STATE_IDLE;
    // 目标状态
    private int mTargetState = STATE_IDLE;
    // 视频宽度缓存
    private int mVideoWidth;
    // 视频高度缓存
    private int mVideoHeight;
    // 视频纵横比分子缓存
    private int mVideoSarNum;
    // 视频纵横比分母缓存
    private int mVideoSarDen;
    // 视频方向角度缓存
    private int mVideoRotationDegree;
    // 目标播放位置
    private int mSeekWhenPrepared;
    // 当前缓冲百分比
    private int mCurrentBufferPercentage;

    @Override
    public boolean isPlaying() {
        return isInPlaybackState() && mIMediaPlayer.isPlaying();
    }

    @Override
    public void start() {
        if (mPlayerGroup != null && !mPlayerGroup.checkFocus(this)) {
            mPlayerGroup.requestFocus(this);
        }

        if (!isLoadedPlayer()) {
            loadPlayer();
        }

        bindRenderView();
        bindInternalListener();

        if (!Media.isEmpty(mMedia)) {
            seekTo(mMedia.getPos());
        }

        if (isInPlaybackState()) {
            mIMediaPlayer.start();
            mCurrentState = STATE_PLAYING;
        } else {
            loadMedia();
        }
        mTargetState = STATE_PLAYING;

        mAbstractControlView.callStart();

        if (mOnOperateListener != null) {
            mOnOperateListener.callStart();
        }
    }

    @Override
    public void pause() {
        if (!Media.isEmpty(mMedia) && isInPlaybackState()) {
            mMedia.setPos(getCurrentPosition());
        }

        if (!mCanPause) return;
        if (isInPlaybackState()) {
            if (mIMediaPlayer.isPlaying()) {
                mIMediaPlayer.pause();
                mCurrentState = STATE_PAUSED;
            }
        }
        mTargetState = STATE_PAUSED;

        mAbstractControlView.callPause();

        if (mOnOperateListener != null) {
            mOnOperateListener.callPause();
        }
    }

    @Override
    public int getDuration() {
        if (isInPlaybackState()) {
            return (int) mIMediaPlayer.getDuration();
        }
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        if (isInPlaybackState()) {
            return (int) mIMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public void seekTo(int pos) {
        //if (!(mCanSeekBackward && mCanSeekForward)) return;
        if (isInPlaybackState()) {
            mIMediaPlayer.seekTo(pos);
            mSeekWhenPrepared = 0;
        } else {
            mSeekWhenPrepared = pos;
        }

        mAbstractControlView.seekStart();

        if (mOnSeekListener != null) {
            mOnSeekListener.seekStart();
        }
    }

    @Override
    public int getBufferPercentage() {
        if (mIMediaPlayer != null) {
            return mCurrentBufferPercentage;
        }
        return 0;
    }

    @Override
    public int getAudioSessionId() {
        if (mIMediaPlayer != null) {
            return mIMediaPlayer.getAudioSessionId();
        }
        return 0;
    }

    @Override
    public boolean canPause() {
        return mCanPause;
    }

    @Override
    public boolean canSeekForward() {
        return mCanSeekForward;
    }

    @Override
    public boolean canSeekBackward() {
        return mCanSeekBackward;
    }

    @Override
    public void stop() {
        if (!Media.isEmpty(mMedia) && isInPlaybackState()) {
            mMedia.setPos(getCurrentPosition());
        }

        if (isInPlaybackState()) {
            if (mIMediaPlayer.isPlaying()) {
                mIMediaPlayer.stop();
                mCurrentState = STATE_PREPARED;
            }
        }
        mTargetState = STATE_PREPARED;

        mAbstractControlView.callStop();

        if (mOnOperateListener != null) {
            mOnOperateListener.callStop();
        }
    }

    /**
     * 重置播放器
     */
    @Override
    public void reset() {
        if (mIMediaPlayer != null) {
            mIMediaPlayer.reset();
            mCurrentState = STATE_IDLE;
        }
        mTargetState = STATE_IDLE;

        mCanPause = false;
        mCanSeekForward = false;
        mCanSeekBackward = false;
        mCurrentBufferPercentage = 0;

        AudioManager am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        am.abandonAudioFocus(null);

        mAbstractControlView.callReset();

        if (mOnOperateListener != null) {
            mOnOperateListener.callReset();
        }
    }

    @Override
    public void release() {
        if (mIMediaPlayer != null) {
            reset();
            mIMediaPlayer.release();
            mIMediaPlayer = null;
        } else {
            if (mPlayerGroup != null) {
                mPlayerGroup.releaseFocus(this);
            }
        }

        if (mPlayerGroup != null) {
            mPlayerGroup.releaseFocus(this);
        }

        mMedia = null;
        mContext = null;
        mPlayerGroup = null;
        mOnPlayerListener = null;

        mAbstractControlView.callRelease();

        if (mOnOperateListener != null) {
            mOnOperateListener.callRelease();
        }
    }

    /**
     * 检查是否已经载入内核和媒体
     * @return
     */
    private boolean isLoadedPlayer() {
        return mIMediaPlayer != null;
    }

    /**
     * 检查是否可以直接切换到播放状态
     * @return
     */
    private boolean isInPlaybackState() {
        return (!Media.isEmpty(mMedia) &&
                mIMediaPlayer != null &&
                mCurrentState != STATE_ERROR &&
                mCurrentState != STATE_IDLE &&
                mCurrentState != STATE_PREPARING);
    }

    /**
     * 载入播放器内核
     */
    private void loadPlayer() {
        if (!isLoadedPlayer()) {
            mIMediaPlayer = PlayerUtil.createPlayer(mContext, mDecoder);
        } else {
            reset();
        }
    }

    /**
     * 载入播放媒体
     */
    private void loadMedia() {
        if (mIMediaPlayer == null) {
            throw new NullPointerException("The player is not loaded yet");
        }

        if (Media.isEmpty(mMedia) || !mRenderCallback.prepared()) {
            return;
        }

        try {
            String scheme = mMedia.getUri().getScheme();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && (TextUtils.isEmpty(scheme) || scheme.equalsIgnoreCase("file"))) {
                IMediaDataSource dataSource = new FileMediaDataSource(new File(mMedia.getUri().toString()));
                mIMediaPlayer.setDataSource(dataSource);
            }  else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                mIMediaPlayer.setDataSource(mContext, mMedia.getUri(), mMedia.getHeaders());
            } else {
                mIMediaPlayer.setDataSource(mMedia.getUri().toString());
            }
            mIMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mIMediaPlayer.setVolume(1.0f, 1.0f);
            mIMediaPlayer.setScreenOnWhilePlaying(true);
            mIMediaPlayer.prepareAsync();

            AudioManager am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
            am.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

            mCurrentState = STATE_PREPARING;

            mAbstractControlView.prepare();
        } catch (Exception ex) {
            mCurrentState = STATE_ERROR;
            mTargetState = STATE_ERROR;

            mAbstractControlView.error(ERROR_IO);

            if (mOnPlayerListener != null) {
                mOnPlayerListener.error(ERROR_IO);
            }
        }
    }

    /**
     * 绑定渲染视图
     */
    private void bindRenderView() {
        if (mIMediaPlayer == null) {
            throw new NullPointerException("The player is not loaded yet");
        }

        mVideoWidth = mIMediaPlayer.getVideoWidth();
        mVideoHeight = mIMediaPlayer.getVideoHeight();
        mVideoSarNum = mIMediaPlayer.getVideoSarNum();
        mVideoSarDen = mIMediaPlayer.getVideoSarDen();

        mAbstractRenderView.bindRenderView(mIMediaPlayer);

        if (mVideoWidth > 0 && mVideoHeight > 0) {
            mAbstractRenderView.setVideoSize(mVideoWidth, mVideoHeight);
            mAbstractRenderView.setVideoSampleAspectRatio(mVideoSarNum, mVideoSarDen);
        }
    }

    /**
     * 设定各种监听器
     */
    private void bindInternalListener() {
        if (mIMediaPlayer == null) {
            throw new NullPointerException("The player is not loaded yet");
        }

        mIMediaPlayer.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer mp) {
                Logger.i(TAG, "onPrepared(" + mp + ")");
                mCurrentState = STATE_PREPARED;

                mCanPause = true;
                mCanSeekBackward = mCanSeekForward = getDuration() != 0;

                // 如果有转跳,则转跳至目标
                if (canSeekBackward() && canSeekForward() && mSeekWhenPrepared != 0) {
                    seekTo(mSeekWhenPrepared);
                }
                // 如果之前调用过start,现在恢复
                if (mTargetState == STATE_PLAYING) {
                    mIMediaPlayer.start();
                    mCurrentState = STATE_PLAYING;
                }

                mAbstractControlView.prepared();
            }
        });
        mIMediaPlayer.setOnInfoListener(new IMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(IMediaPlayer iMediaPlayer, int what, int extra) {
                Logger.i(TAG, "Info: " + PlayerUtil.getInfo(what) + "," + extra);
                switch (what) {
                    case INFO_UNKNOWN:
                        break;
                    case INFO_STARTED_AS_NEXT:
                        break;
                    case INFO_VIDEO_RENDERING_START:
                        mAbstractControlView.playStart();

                        if (mOnPlayerListener != null) {
                            mOnPlayerListener.playStart();
                        }
                        break;
                    case INFO_VIDEO_TRACK_LAGGING:
                        break;
                    case INFO_BUFFERING_START:
                        mAbstractControlView.bufferStart();

                        if (mOnBufferListener != null) {
                            mOnBufferListener.bufferStart();
                        }
                        break;
                    case INFO_BUFFERING_END:
                        mAbstractControlView.bufferEnd();

                        if (mOnBufferListener != null) {
                            mOnBufferListener.bufferEnd();
                        }
                        break;
                    case INFO_NETWORK_BANDWIDTH:
                        break;
                    case INFO_BAD_INTERLEAVING:
                        break;
                    case INFO_NOT_SEEKABLE:
                        break;
                    case INFO_METADATA_UPDATE:
                        break;
                    case INFO_TIMED_TEXT_ERROR:
                        break;
                    case INFO_UNSUPPORTED_SUBTITLE:
                        break;
                    case INFO_SUBTITLE_TIMED_OUT:
                        break;
                    case INFO_VIDEO_ROTATION_CHANGED:
                        mVideoRotationDegree = extra;
                        mAbstractRenderView.setVideoRotation(mVideoRotationDegree);
                        break;
                    case INFO_AUDIO_RENDERING_START:
                        break;
                    case INFO_AUDIO_DECODED_START:
                        break;
                    case INFO_VIDEO_DECODED_START:
                        break;
                    case INFO_OPEN_INPUT:
                        break;
                    case INFO_FIND_STREAM_INFO:
                        break;
                    case INFO_COMPONENT_OPEN:
                        break;
                    case INFO_MEDIA_ACCURATE_SEEK_COMPLETE:
                        break;
                    default:
                        break;
                }
                // TODO: 2017/10/28  视频播放信息,回调
                return true;
            }
        });
        mIMediaPlayer.setOnVideoSizeChangedListener(new IMediaPlayer.OnVideoSizeChangedListener() {
            @Override
            public void onVideoSizeChanged(IMediaPlayer mp, int width, int height, int sarNum, int sarDen) {
                Logger.i(TAG, "onVideoSizeChanged(" + mp + ", " + width + ", " + height + ", " + sarNum + ", " + sarDen + ")");
                mVideoWidth = mp.getVideoWidth();
                mVideoHeight = mp.getVideoHeight();
                mVideoSarNum = mp.getVideoSarNum();
                mVideoSarDen = mp.getVideoSarDen();
                if (mVideoWidth > 0 && mVideoHeight > 0) {
                    mAbstractRenderView.setVideoSize(mVideoWidth, mVideoHeight);
                    mAbstractRenderView.setVideoSampleAspectRatio(sarNum, sarDen);
                }
            }
        });
        mIMediaPlayer.setOnBufferingUpdateListener(new IMediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(IMediaPlayer mp, int percent) {
                Logger.i(TAG, "onBufferingUpdate(" + (IjkMediaPlayer)mp + ", " + percent + ")");
                mCurrentBufferPercentage = percent;
                mAbstractControlView.bufferUpdate(percent);

                if (mOnBufferListener != null) {
                    mOnBufferListener.bufferUpdate(percent);
                }
            }
        });
        mIMediaPlayer.setOnSeekCompleteListener(new IMediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(IMediaPlayer mp) {
                Logger.i(TAG, "onSeekComplete(" + mp + ")");
                mAbstractControlView.seekEnd();

                if (mOnSeekListener != null) {
                    mOnSeekListener.seekEnd();
                }
            }
        });
        mIMediaPlayer.setOnTimedTextListener(new IMediaPlayer.OnTimedTextListener() {
            @Override
            public void onTimedText(IMediaPlayer mp, IjkTimedText text) {
                Logger.i(TAG, "onTimedText(" + mp + ", " + text + ")");
                // TODO: 2017/10/28  字幕流,回调
            }
        });
        mIMediaPlayer.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(IMediaPlayer mp) {
                Logger.i(TAG, "onCompletion(" + mp + ")");
                mCurrentState = STATE_PLAYBACK_COMPLETED;
                mTargetState = STATE_PLAYBACK_COMPLETED;
                mAbstractControlView.playComplete();

                if (mOnPlayerListener != null) {
                    mOnPlayerListener.playComplete();
                }
            }
        });
        mIMediaPlayer.setOnErrorListener(new IMediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(IMediaPlayer iMediaPlayer, int what, int extra) {
                Logger.e(TAG, "Error: " + PlayerUtil.getError(what) + "," + extra);
                mCurrentState = STATE_ERROR;
                mTargetState = STATE_ERROR;
                switch (what) {
                    case ERROR_UNKNOWN:
                        break;
                    case ERROR_SERVER_DIED:
                        break;
                    case ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                        break;
                    case ERROR_IO:
                        break;
                    case ERROR_MALFORMED:
                        break;
                    case ERROR_UNSUPPORTED:
                        break;
                    case ERROR_TIMED_OUT:
                        break;
                    default:
                        break;
                }
                mAbstractControlView.error(what);

                if (mOnPlayerListener != null) {
                    mOnPlayerListener.error(what);
                }
                return true;
            }
        });
    }

    /* ========================= Kernel_End ========================= */

}
