package com.liux.glide.video;

import com.bumptech.glide.load.Key;

import java.net.URL;
import java.security.MessageDigest;

/**
 * 自定义的视频缩略图显示过程
 * https://juejin.im/entry/59b2076ff265da247e7ddbf1
 * Created by Liux on 2017/9/13.
 */

public class Video implements Key {

    public static Video from(URL url) {
        return new Video(url);
    }

    public static Video from(String url) {
        return new Video(url);
    }

    private final URL url;
    private final String stringUrl;
    private volatile byte[] cacheBytes;

    public Video(URL url) {
        this.url = url;
        this.stringUrl = null;
    }

    public Video(String stringUrl) {
        this.url = null;
        this.stringUrl = stringUrl;
    }

    @Override
    public void updateDiskCacheKey(MessageDigest messageDigest) {
        byte[] bytes = getCacheBytes();
        messageDigest.update(bytes);
    }

    public String getStringUrl() {
        return stringUrl != null ? stringUrl : url.toString();
    }

    private byte[] getCacheBytes() {
        if(cacheBytes == null) {
            cacheBytes = getCacheKey().getBytes(CHARSET);
        }
        return cacheBytes;
    }

    private String getCacheKey() {
        return "video:" + getStringUrl();
    }
}