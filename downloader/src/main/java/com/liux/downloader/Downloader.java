package com.liux.downloader;

import android.content.Context;

import com.liux.downloader.listener.TaskCallBack;
import com.liux.downloader.local.DownloaderLocal;

import java.io.File;
import java.util.Map;

/**
 * Created by Liux on 2017/12/8.
 */

public class Downloader {
    private static volatile Downloader mInstance;

    /**
     * 是否已经初始化
     * @return
     */
    public static boolean isInitialize() {
        return mInstance != null;
    }

    /**
     * 初始化Downloader
     * @param context
     */
    public static void initialize(Context context) {
        initialize(context, new Config.Builder().
                build());
    }

    /**
     * 初始化Downloader
     * @param context
     */
    public static void initialize(Context context, Config config) {
        if (mInstance != null) return;
        synchronized(Downloader.class) {
            if (mInstance != null) return;
            mInstance = new Downloader(context, config);
        }
    }

    /**
     * 获取实例
     * @return
     */
    public static Downloader instance() {
        if (mInstance == null) throw new NullPointerException("Downloader has not been initialized");
        return mInstance;
    }

    private static final String TAG = "[Downloader]";

    private DownloaderLocal mDownloaderLocal;

    public Downloader(Context context, Config config) {
        mDownloaderLocal = new DownloaderLocal(context, config);
    }

    /**
     * 添加回调接口
     * @param callBack
     */
    public void addCallBack(TaskCallBack callBack) {
        mDownloaderLocal.addCallBack(callBack);
    }

    /**
     * 移除回调接口
     * @param callBack
     */
    public void removeCallBack(TaskCallBack callBack) {
        mDownloaderLocal.removeCallBack(callBack);
    }

    /**
     * 获取任务实例
     * @param url URL
     * @return
     */
    public Task get(String url) {
        return get(url, null,null);
    }

    /**
     * 获取任务实例
     * @param url URL
     * @param dir 存储目录
     * @return
     */
    public Task get(String url, File dir) {
        return get(url, dir, null);
    }

    /**
     * 获取任务实例
     * @param url URL
     * @param header 自定义请求头
     * @return
     */
    public Task get(String url, Map<String, String> header) {
        return get(url, null, header);
    }

    /**
     * 获取任务实例
     * @param url URL
     * @param dir 存储目录
     * @param header 自定义请求头
     * @return
     */
    public Task get(String url, File dir, Map<String, String> header) {
        return mDownloaderLocal.getTask(url, dir, header);
    }

    /**
     * 是否已经连接到远程服务
     * @return
     */
    public boolean isConnected() {
        return mDownloaderLocal.isConnected();
    }
}
