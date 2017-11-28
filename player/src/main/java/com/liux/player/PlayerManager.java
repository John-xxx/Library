package com.liux.player;

import com.liux.player.view.AbstractPlayerView;
import com.liux.player.view.FullScreenPlayerView;
import com.liux.player.view.SmallScreenPlayerView;

/**
 * 管理播放器全屏/小屏状态
 * Created by Liux on 2017/11/24.
 */

public class PlayerManager {
    // 播放器内核类型
    private static PlayerView.Decoder mDecoder = PlayerView.Decoder.FFMPEG;

    public static PlayerView.Decoder getDecoder() {
        return mDecoder;
    }

    public static void setDecoder(PlayerView.Decoder decoder) {
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

    private static FullScreenPlayerView mFullScreenPlayerView;

    public static boolean checkFullScreen(AbstractPlayerView playerView) {
        if (playerView == null) return false;
        if (mFullScreenPlayerView == null) return false;
        return playerView.getMedia().equals(mFullScreenPlayerView.getMedia());
    }

    public static void openFullScreen(AbstractPlayerView playerView) {
        if (playerView == null) return;
        if (mFullScreenPlayerView == null) {
            mFullScreenPlayerView = new FullScreenPlayerView(playerView);
        }
        boolean isPlaying = playerView.isPlaying();
        playerView.stop();
        mFullScreenPlayerView.setMedia(playerView.getMedia());
        mFullScreenPlayerView.seekTo(playerView.getMedia().getPos());
        if (isPlaying) {
            mFullScreenPlayerView.start();
        }
    }

    public static void closeFullScreen(AbstractPlayerView playerView) {
        if (playerView == null) return;
        if (checkFullScreen(playerView)) {
            boolean isPlaying = mFullScreenPlayerView.isPlaying();
            mFullScreenPlayerView.stop();
            playerView.seekTo(mFullScreenPlayerView.getMedia().getPos());
            if (isPlaying) {
                playerView.start();
            }
            mFullScreenPlayerView.release();
            mFullScreenPlayerView = null;
        }
    }

    private static SmallScreenPlayerView mSmallScreenPlayerView;

    public static boolean checkSmallScreen(AbstractPlayerView playerView) {
        if (playerView == null) return false;
        if (mSmallScreenPlayerView == null) return false;
        return playerView.getMedia().equals(mSmallScreenPlayerView.getMedia());
    }

    public static void openSmallScreen(AbstractPlayerView playerView) {
        if (playerView == null) return;
        if (mSmallScreenPlayerView == null) {
            mSmallScreenPlayerView = new SmallScreenPlayerView(playerView);
        }
        boolean isPlaying = playerView.isPlaying();
        playerView.stop();
        mSmallScreenPlayerView.setMedia(playerView.getMedia());
        mSmallScreenPlayerView.seekTo(playerView.getMedia().getPos());
        if (isPlaying) {
            mSmallScreenPlayerView.start();
        }
    }

    public static void closeSmallScreen(AbstractPlayerView playerView) {
        if (playerView == null) return;
        if (checkSmallScreen(playerView)) {
            boolean isPlaying = mSmallScreenPlayerView.isPlaying();
            mSmallScreenPlayerView.stop();
            playerView.seekTo(mSmallScreenPlayerView.getMedia().getPos());
            if (isPlaying) {
                playerView.start();
            }
            mSmallScreenPlayerView.release();
            mSmallScreenPlayerView = null;
        }
    }
}
