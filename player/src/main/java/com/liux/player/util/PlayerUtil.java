package com.liux.player.util;

import android.content.Context;

import com.liux.player.PlayerView;

import tv.danmaku.ijk.media.exo.IjkExoMediaPlayer;
import tv.danmaku.ijk.media.player.AndroidMediaPlayer;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.TextureMediaPlayer;

/**
 * Created by Liux on 2017/10/27.
 */

public class PlayerUtil {

    /**
     * 创建一个 IMediaPlayer
     * @param type
     * @return
     */
    public static IMediaPlayer createPlayer(Context context, PlayerView.Decoder type) {
        IMediaPlayer iMediaPlayer;
        switch (type) {
            case FFMPEG:
                IjkMediaPlayer ijkMediaPlayer = new IjkMediaPlayer();
                ijkMediaPlayer.native_setLogLevel(IjkMediaPlayer.IJK_LOG_WARN);

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
                ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "showPlay-on-prepared", 0);

                ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "http-detect-range-support", 0);

                ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48);

                boolean EnableDetachedSurfaceTextureView = false;
                if (EnableDetachedSurfaceTextureView) {
                    iMediaPlayer = new TextureMediaPlayer(ijkMediaPlayer);
                } else {
                    iMediaPlayer = ijkMediaPlayer;
                }
                break;
            case ANDROID:
                iMediaPlayer = new AndroidMediaPlayer();
                break;
            case EXO:
                iMediaPlayer = new IjkExoMediaPlayer(context.getApplicationContext());
                break;
            default:
                iMediaPlayer = null;
                break;
        }
        return iMediaPlayer;
    }

    /**
     * 取错误信息描述
     * @param what
     * @return
     */
    public static String getError(int what) {
        String error;
        switch (what) {
            case PlayerView.ERROR_UNKNOWN:
                error = "ERROR_UNKNOWN(1)";
                break;
            case PlayerView.ERROR_SERVER_DIED:
                error = "ERROR_SERVER_DIED";
                break;
            case PlayerView.ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                error = "ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK";
                break;
            case PlayerView.ERROR_IO:
                error = "ERROR_IO";
                break;
            case PlayerView.ERROR_MALFORMED:
                error = "ERROR_MALFORMED";
                break;
            case PlayerView.ERROR_UNSUPPORTED:
                error = "ERROR_UNSUPPORTED";
                break;
            case PlayerView.ERROR_TIMED_OUT:
                error = "ERROR_TIMED_OUT";
                break;
            default:
                error = "ERROR_UNKNOWN(" + what + ")";
                break;
        }
        return error;
    }

    /**
     * 取回调信息描述
     * @param what
     * @return
     */
    public static String getInfo(int what) {
        String info;
        switch (what) {
            case PlayerView.INFO_UNKNOWN:
                info = "INFO_UNKNOWN(1)";
                break;
            case PlayerView.INFO_STARTED_AS_NEXT:
                info = "INFO_STARTED_AS_NEXT";
                break;
            case PlayerView.INFO_VIDEO_RENDERING_START:
                info = "INFO_VIDEO_RENDERING_START";
                break;
            case PlayerView.INFO_VIDEO_TRACK_LAGGING:
                info = "INFO_VIDEO_TRACK_LAGGING";
                break;
            case PlayerView.INFO_BUFFERING_START:
                info = "INFO_BUFFERING_START";
                break;
            case PlayerView.INFO_BUFFERING_END:
                info = "INFO_BUFFERING_END";
                break;
            case PlayerView.INFO_NETWORK_BANDWIDTH:
                info = "INFO_NETWORK_BANDWIDTH";
                break;
            case PlayerView.INFO_BAD_INTERLEAVING:
                info = "INFO_BAD_INTERLEAVING";
                break;
            case PlayerView.INFO_NOT_SEEKABLE:
                info = "INFO_NOT_SEEKABLE";
                break;
            case PlayerView.INFO_METADATA_UPDATE:
                info = "INFO_METADATA_UPDATE";
                break;
            case PlayerView.INFO_TIMED_TEXT_ERROR:
                info = "INFO_TIMED_TEXT_ERROR";
                break;
            case PlayerView.INFO_UNSUPPORTED_SUBTITLE:
                info = "INFO_UNSUPPORTED_SUBTITLE";
                break;
            case PlayerView.INFO_SUBTITLE_TIMED_OUT:
                info = "INFO_SUBTITLE_TIMED_OUT";
                break;
            case PlayerView.INFO_VIDEO_ROTATION_CHANGED:
                info = "INFO_VIDEO_ROTATION_CHANGED";
                break;
            case PlayerView.INFO_AUDIO_RENDERING_START:
                info = "INFO_AUDIO_RENDERING_START";
                break;
            case PlayerView.INFO_AUDIO_DECODED_START:
                info = "INFO_AUDIO_DECODED_START";
                break;
            case PlayerView.INFO_VIDEO_DECODED_START:
                info = "INFO_VIDEO_DECODED_START";
                break;
            case PlayerView.INFO_OPEN_INPUT:
                info = "INFO_OPEN_INPUT";
                break;
            case PlayerView.INFO_FIND_STREAM_INFO:
                info = "INFO_FIND_STREAM_INFO";
                break;
            case PlayerView.INFO_COMPONENT_OPEN:
                info = "INFO_COMPONENT_OPEN";
                break;
            case PlayerView.INFO_MEDIA_ACCURATE_SEEK_COMPLETE:
                info = "INFO_MEDIA_ACCURATE_SEEK_COMPLETE";
                break;
            default:
                info = "INFO_UNKNOWN(" + what + ")";
                break;
        }
        return info;
    }
}
