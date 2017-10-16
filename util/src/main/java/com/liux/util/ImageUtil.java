package com.liux.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Liux on 2016/9/7.
 */
public class ImageUtil {

    /**
     * 使用ContentProvider读取SD卡最近图片。
     */
    public static List<String> getAllImagePaths(Context context, int maxCount) {
        Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String key_MIME_TYPE = MediaStore.Images.Media.MIME_TYPE;
        String key_DATA = MediaStore.Images.Media.DATA;

        ContentResolver mContentResolver = context.getContentResolver();

        Cursor cursor = mContentResolver.query(mImageUri, new String[]{key_DATA},
                key_MIME_TYPE + "=? or " + key_MIME_TYPE + "=? or " + key_MIME_TYPE + "=? or " + key_MIME_TYPE + "=?",
                new String[]{"image/jpg", "image/jpeg", "image/png", "image/bmp"},
                MediaStore.Images.Media.DATE_MODIFIED);

        List<String> paths = new ArrayList();
        if (cursor != null) {
            if (cursor.moveToLast()) {
                paths = new ArrayList();

                while (true) {
                    String path = cursor.getString(0);
                    paths.add(path);

                    if (!cursor.moveToPrevious()) break;
                    if (maxCount != 0 && paths.size() >= maxCount) break;
                }
            }
            cursor.close();
        }
        return paths;
    }

    /**
     * 使用ContentProvider读取SD卡最近图片。
     */
    public static List<String> getFolderImagePaths(Context context, String folder) {
        List<String> paths = getAllImagePaths(context, Integer.MAX_VALUE);

        List<String> result = new ArrayList();
        for (String path : paths) {
            if (path.startsWith(folder) && path.replace(folder + "/", "").indexOf("/") == -1) {
                result.add(path);
            }
        }
        return result;
    }

    /**
     * 保存Bitmap至Uri */
    public static boolean saveBitmap(Bitmap bitmap, Uri uri) {
        File file = new File(uri.getPath());
        try {
            if (file.exists()) file.delete();
            FileOutputStream fos = new FileOutputStream(file);
            BufferedOutputStream out = new BufferedOutputStream(fos);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
