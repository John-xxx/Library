package com.liux.downloader;

import android.content.Context;

/**
 * Created by Liux on 2017/12/8.
 */

public class Downloader {
    private static volatile Downloader mInstance;

    public static boolean isInitialize() {
        return mInstance != null;
    }

    public static Downloader instance() {
        if (mInstance == null) throw new NullPointerException("Downloader has not been initialized");
        return mInstance;
    }

    public static void initialize(Context context) {
        if (mInstance != null) return;
        synchronized(Downloader.class) {
            if (mInstance != null) return;
            mInstance = new Downloader(context);
        }
    }

    public static void setConfig(Config config) {

    }

    private static final String TAG = "[Downloader]";

    private Context mContext;

    private DownloaderLocal mDownloaderLocal;

    public Downloader(Context context) {
        mContext = context.getApplicationContext();

        mDownloaderLocal = new DownloaderLocal();
    }
}
