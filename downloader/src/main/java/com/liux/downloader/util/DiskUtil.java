package com.liux.downloader.util;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Created by Liux on 2017/12/13.
 */

public class DiskUtil {

    /**
     * 获取默认的存储目录
     * 1.getExternalFilesDir
     * 2.getFilesDir
     * @param context
     * @return
     */
    public static File getStorageDir(Context context) {
        File dir;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            dir = context.getExternalFilesDir(null);
        } else {
            dir = context.getFilesDir();
        }
        return dir;
    }
}
