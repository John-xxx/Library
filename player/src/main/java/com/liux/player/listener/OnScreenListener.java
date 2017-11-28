package com.liux.player.listener;

/**
 * Created by Liux on 2017/11/26.
 */

public interface OnScreenListener {

    /**
     * 播放焦点改变(组播事件)
     */
    void changeFocus();

    /**
     * 屏幕状态改变(正常/全屏/小屏)
     */
    void changeScreen();
}
