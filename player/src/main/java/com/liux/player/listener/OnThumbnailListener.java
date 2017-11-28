package com.liux.player.listener;

import android.widget.ImageView;

import com.liux.player.Media;

/**
 * Created by Liux on 2017/11/26.
 */

public interface OnThumbnailListener {

    /**
     * 加载缩略图
     * @param imageView
     * @param media
     */
    void onThumbnail(ImageView imageView, Media media);
}
