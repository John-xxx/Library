package com.liux.player;

import com.liux.player.view.AbstractPlayerView;

/**
 * 组播控制器
 * 用于控制管理一组视图中各个播放器视图
 * Created by Liux on 2017/10/26.
 */

public class PlayerGroup {

    // 是否需要恢复播放
    private boolean mNeedRecoverPlay = false;
    // 当前具有焦点的播放器
    private AbstractPlayerView mFocusPlayerView;

    public void onResume() {
        if (mFocusPlayerView == null) return;
        if (mNeedRecoverPlay) {
            mFocusPlayerView.start();
        }
    }

    public void onPause() {
        if (mFocusPlayerView == null) return;
        if (mFocusPlayerView.isPlaying()) {
            mFocusPlayerView.pause();
            mNeedRecoverPlay = true;
        } else {
            mNeedRecoverPlay = false;
        }
    }

    public void onDestroy() {
        if (mFocusPlayerView == null) return;
        mFocusPlayerView.stop();
        mFocusPlayerView.release();
        mFocusPlayerView = null;
    }

    public boolean onBackPressed() {
        if (mFocusPlayerView == null) return false;
        return mFocusPlayerView.onBackPressed();
    }

    /**
     * 检查组播播放器焦点
     * @param playerView
     * @return
     */
    public boolean checkFocus(AbstractPlayerView playerView) {
        // 不为空并且 是 上一个具有检点的目标
        if (playerView != null && playerView == mFocusPlayerView) {
            // 目标媒体不为空则比较媒体信息
            if (!Media.isEmpty(playerView.getMedia()) && mFocusPlayerView != null) {
                if (playerView.getMedia().equals(mFocusPlayerView.getMedia())) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    /**
     * 申请组播播放器焦点
     * @param playerView
     */
    public void requestFocus(AbstractPlayerView playerView) {
        // 停止已有焦点的播放器
        if (mFocusPlayerView != null) {
            mFocusPlayerView.getGroupCallback().changeFocus(playerView);
        }

        mFocusPlayerView = playerView;
    }

    /**
     * 释放组播播放器焦点
     * @param playerView
     */
    public void releaseFocus(AbstractPlayerView playerView) {
        if (mFocusPlayerView == playerView) {
            mFocusPlayerView = null;
        }
    }

    /**
     * 组播事件回调
     */
    public interface Callback {

        /**
         * 焦点改变回调,在被代替焦点后和主动取消焦点后回调
         */
        void changeFocus(AbstractPlayerView playerView);
    }
}
