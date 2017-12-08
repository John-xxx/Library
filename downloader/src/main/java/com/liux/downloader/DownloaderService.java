package com.liux.downloader;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Liux on 2017/12/8.
 */

public class DownloaderService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
