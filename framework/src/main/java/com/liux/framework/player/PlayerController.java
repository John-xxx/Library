package com.liux.framework.player;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.liux.framework.player.listener.OnPlayerListener;
import com.liux.framework.player.util.FileMediaDataSource;
import com.liux.framework.player.view.ControlView;
import com.liux.framework.player.view.RenderView;

import java.io.File;
import java.util.Map;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.IjkTimedText;
import tv.danmaku.ijk.media.player.TextureMediaPlayer;
import tv.danmaku.ijk.media.player.misc.IMediaDataSource;

/**
 * Created by Liux on 2017/9/17.
 */

public class PlayerController implements Player {
    private static final String TAG = "PlayerController";

    private static final int STATE_ERROR = -1;
    private static final int STATE_IDLE = 0;
    private static final int STATE_PREPARING = 1;
    private static final int STATE_PREPARED = 2;
    private static final int STATE_PLAYING = 3;
    private static final int STATE_PAUSED = 4;
    private static final int STATE_PLAYBACK_COMPLETED = 5;

    // 当前状态
    private int mCurrentState = STATE_IDLE;
    // 目标状态
    private int mTargetState = STATE_IDLE;

    // 播放目标
    private Uri mUri;
    // 跨域请求头
    private Map<String, String> mHeaders;

    // 上下文信息
    private Context mContext;
    // 播放器组件
    private IMediaPlayer mIMediaPlayer;

    // 渲染视图
    private RenderView mRenderView;
    // 控制视图
    private ControlView mControlView;
    // 播放器状态回调
    private OnPlayerListener mOnPlayerListener;

    // 是否能暂停
    private boolean mCanPause;
    // 是否能够后退
    private boolean mCanSeekBackward;
    // 是否能够前进
    private boolean mCanSeekForward;

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

    public PlayerController(Context context) {
        mContext = context.getApplicationContext();
    }

    @Override
    public void load(String path) {
        load(Uri.parse(path));
    }

    @Override
    public void load(Uri uri) {
        load(uri, null);
    }

    @Override
    public void load(Uri uri, Map<String, String> headers) {
        mUri = uri;
        mHeaders = headers;

        openVideo();
    }

    @Override
    public void start() {
        if (isInPlaybackState()) {
            mIMediaPlayer.start();
            mCurrentState = STATE_PLAYING;
        }
        mTargetState = STATE_PLAYING;
    }

    @Override
    public void stop() {
        if (isInPlaybackState()) {
            if (mIMediaPlayer.isPlaying()) {
                mIMediaPlayer.pause();
                mCurrentState = STATE_PAUSED;
            }
        }
        mTargetState = STATE_PAUSED;
    }

    @Override
    public void reset() {
        release(true);
    }

    @Override
    public boolean playing() {
        return isInPlaybackState() && mIMediaPlayer.isPlaying();
    }

    @Override
    public void seekTo(int pos) {
        if (isInPlaybackState()) {
            mIMediaPlayer.seekTo(pos);
            mSeekWhenPrepared = 0;
        } else {
            mSeekWhenPrepared = pos;
        }
    }

    @Override
    public int getCurrentPosition() {
        if (isInPlaybackState()) {
            return (int) mIMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public int getDuration() {
        if (isInPlaybackState()) {
            return (int) mIMediaPlayer.getDuration();
        }
        return -1;
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
        return mIMediaPlayer.getAudioSessionId();
    }

    @Override
    public boolean canPause() {
        return mCanPause;
    }

    @Override
    public boolean canSeekBackward() {
        return mCanSeekBackward;
    }

    @Override
    public boolean canSeekForward() {
        return mCanSeekForward;
    }

    @Override
    public void setRenderView(RenderView view) {
        mRenderView = view;

        openVideo();
    }

    @Override
    public void setControlView(ControlView view) {
        mControlView = view;
    }

    @Override
    public void setOnPlayerListener(OnPlayerListener listener) {
        mOnPlayerListener = listener;
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void openVideo() {
        if (mUri == null || mRenderView == null) {
            // not ready for playback just yet, will try again later
            return;
        }

        // we shouldn't clear the target state, because somebody might have called start() previously
        release(false);

        AudioManager am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        am.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        try {
            mIMediaPlayer = createPlayer();

            initInternalListener();

            mCurrentBufferPercentage = 0;

            String scheme = mUri.getScheme();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && (TextUtils.isEmpty(scheme) || scheme.equalsIgnoreCase("file"))) {
                IMediaDataSource dataSource = new FileMediaDataSource(new File(mUri.toString()));
                mIMediaPlayer.setDataSource(dataSource);
            }  else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                mIMediaPlayer.setDataSource(mContext, mUri, mHeaders);
            } else {
                mIMediaPlayer.setDataSource(mUri.toString());
            }

            mRenderView.bindPlayer(mIMediaPlayer);

            mIMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mIMediaPlayer.setScreenOnWhilePlaying(true);

            mIMediaPlayer.prepareAsync();

            mCurrentState = STATE_PREPARING;

            // TODO: 2017/9/17 初始化开始,回调
        } catch (Exception ex) {
            Log.w(TAG, "Unable to open content: " + mUri, ex);
            mCurrentState = STATE_ERROR;
            mTargetState = STATE_ERROR;
            // TODO: 2017/9/17 初始化报错,回调
        } finally {

        }
    }

    /**
     * release the media player in any state
     */
    public void release(boolean clearTargetState) {
        if (mIMediaPlayer != null) {
            mIMediaPlayer.reset();
            mIMediaPlayer.release();
            mIMediaPlayer = null;

            mCurrentState = STATE_IDLE;
            if (clearTargetState) {
                mTargetState = STATE_IDLE;
            }
            AudioManager am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
            am.abandonAudioFocus(null);
        }
    }

    /**
     * 创建一个 IMediaPlayer
     * @return
     */
    private IMediaPlayer createPlayer() {
        IjkMediaPlayer ijkMediaPlayer = new IjkMediaPlayer();
        ijkMediaPlayer.native_setLogLevel(IjkMediaPlayer.IJK_LOG_DEBUG);

        boolean UsingMediaCodec = false;
        if (!UsingMediaCodec) {
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 0);
        } else {
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);

            boolean UsingMediaCodecAutoRotate = false;
            if (!UsingMediaCodecAutoRotate) {
                ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 0);
            } else {
                ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 1);
            }

            boolean MediaCodecHandleResolutionChange = false;
            if (!MediaCodecHandleResolutionChange) {
                ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", 0);
            } else {
                ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", 1);
            }
        }

        boolean UsingOpenSLES = false;
        if (!UsingOpenSLES) {
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", 0);
        } else {
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", 1);
        }

        String pixelFormat = "";
        if (pixelFormat == null || pixelFormat.isEmpty()) {
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", IjkMediaPlayer.SDL_FCC_RV32);
        } else {
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", pixelFormat);
        }

        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 1);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 0);

        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "http-detect-range-support", 0);

        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48);

        IMediaPlayer iMediaPlayer = ijkMediaPlayer;

        boolean EnableDetachedSurfaceTextureView = false;
        if (EnableDetachedSurfaceTextureView) {
            iMediaPlayer = new TextureMediaPlayer(iMediaPlayer);
        }

        return iMediaPlayer;
    }

    /**
     * 设定各种监听器
     */
    private void initInternalListener() {
        mIMediaPlayer.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer mp) {
                mCurrentState = STATE_PREPARED;

                mVideoWidth = mp.getVideoWidth();
                mVideoHeight = mp.getVideoHeight();
                mVideoSarNum = mp.getVideoSarNum();
                mVideoSarDen = mp.getVideoSarDen();

                // 如果有转跳,则转跳至目标
                if (mSeekWhenPrepared != 0) {
                    seekTo(mSeekWhenPrepared);
                }

                if (mVideoWidth > 0 && mVideoHeight > 0) {
                    if (mRenderView != null) {
                        mRenderView.setVideoSize(mVideoWidth, mVideoHeight);
                        mRenderView.setVideoSampleAspectRatio(mVideoSarNum, mVideoSarDen);
                    }
                }

                // 如果之前调用过start,现在恢复
                if (mTargetState == STATE_PLAYING) {
                    start();
                }

                // TODO: 2017/9/17  准备完毕,回调
            }
        });
        mIMediaPlayer.setOnVideoSizeChangedListener(new IMediaPlayer.OnVideoSizeChangedListener() {
            @Override
            public void onVideoSizeChanged(IMediaPlayer mp, int width, int height, int sarNum, int sarDen) {
                mVideoWidth = mp.getVideoWidth();
                mVideoHeight = mp.getVideoHeight();
                mVideoSarNum = mp.getVideoSarNum();
                mVideoSarDen = mp.getVideoSarDen();

                if (mVideoWidth > 0 && mVideoHeight > 0) {
                    if (mRenderView != null) {
                        mRenderView.setVideoSize(mVideoWidth, mVideoHeight);
                        mRenderView.setVideoSampleAspectRatio(sarNum, sarDen);
                    }
                }
            }
        });
        mIMediaPlayer.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(IMediaPlayer iMediaPlayer) {
                mCurrentState = STATE_PLAYBACK_COMPLETED;
                mTargetState = STATE_PLAYBACK_COMPLETED;
                // TODO: 2017/9/17  播放完成,回调
            }
        });
        mIMediaPlayer.setOnErrorListener(new IMediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(IMediaPlayer iMediaPlayer, int what, int extra) {
                Log.d(TAG, "Error: " + what + "," + extra);
                mCurrentState = STATE_ERROR;
                mTargetState = STATE_ERROR;
                // TODO: 2017/9/17   播放错误,回调
                switch (what) {
                    case IMediaPlayer.MEDIA_ERROR_UNKNOWN:
                        break;
                    case IMediaPlayer.MEDIA_ERROR_SERVER_DIED:
                        break;
                    case IMediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                        break;
                    case IMediaPlayer.MEDIA_ERROR_IO:
                        break;
                    case IMediaPlayer.MEDIA_ERROR_MALFORMED:
                        break;
                    case IMediaPlayer.MEDIA_ERROR_UNSUPPORTED:
                        break;
                    case IMediaPlayer.MEDIA_ERROR_TIMED_OUT:
                        break;
                }
                return true;
            }
        });
        mIMediaPlayer.setOnInfoListener(new IMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(IMediaPlayer iMediaPlayer, int what, int extra) {
                // TODO: 2017/9/17  视频播放信息,回调
                switch (what) {
                    case IMediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
                        break;
                    case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                        break;
                    case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                        break;
                    case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                        break;
                    case IMediaPlayer.MEDIA_INFO_NETWORK_BANDWIDTH:
                        break;
                    case IMediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
                        break;
                    case IMediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
                        break;
                    case IMediaPlayer.MEDIA_INFO_METADATA_UPDATE:
                        break;
                    case IMediaPlayer.MEDIA_INFO_UNSUPPORTED_SUBTITLE:
                        break;
                    case IMediaPlayer.MEDIA_INFO_SUBTITLE_TIMED_OUT:
                        break;
                    case IMediaPlayer.MEDIA_INFO_VIDEO_ROTATION_CHANGED:
                        mVideoRotationDegree = extra;
                        if (mRenderView != null) {
                            mRenderView.setVideoRotation(mVideoRotationDegree);
                        }
                        break;
                    case IMediaPlayer.MEDIA_INFO_AUDIO_RENDERING_START:
                        break;
                }
                return true;
            }
        });
        mIMediaPlayer.setOnBufferingUpdateListener(new IMediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int percent) {
                mCurrentBufferPercentage = percent;
                // TODO: 2017/9/17  缓冲信息改变,回调
            }
        });
        mIMediaPlayer.setOnSeekCompleteListener(new IMediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(IMediaPlayer iMediaPlayer) {
                // TODO: 2017/9/17  跳转完成,回调
            }
        });
        mIMediaPlayer.setOnTimedTextListener(new IMediaPlayer.OnTimedTextListener() {
            @Override
            public void onTimedText(IMediaPlayer mp, IjkTimedText text) {

            }
        });
    }

    /**
     * 检查是否可以直接切换到播放状态
     * @return
     */
    private boolean isInPlaybackState() {
        return (mIMediaPlayer != null &&
                mCurrentState != STATE_ERROR &&
                mCurrentState != STATE_IDLE &&
                mCurrentState != STATE_PREPARING);
    }
}
