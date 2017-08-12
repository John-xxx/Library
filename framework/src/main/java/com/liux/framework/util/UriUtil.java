package com.liux.framework.util;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.io.IOException;

/**
 * Created by Liux on 2016/12/8.
 */
public class UriUtil {

    public static Uri getFileUri(Context context, String name) {
        return getFileUri(context, name, false);
    }

    public static Uri getFileUri(Context context, String name, boolean create) {
        /* Environment.DIRECTORY_PICTURES */
        File filesDir = context.getExternalFilesDir(null);
        if (filesDir == null) filesDir = context.getFilesDir();
        if (create && !filesDir.exists()) filesDir.mkdirs();
        File file = new File(filesDir.getPath()+"/" + name);
        if (create && !file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Uri.fromFile(file);
    }

    public static Uri getProviderFileUri(Context context, String name) {
        return getProviderFileUri(context, name, false);
    }

    public static Uri getProviderFileUri(Context context, String name, boolean create) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return getFileUri(context, name, create);
        }
        /* Environment.DIRECTORY_PICTURES */
        File filesDir = context.getExternalFilesDir(null);
        if (filesDir == null) filesDir = context.getFilesDir();
        if (create && !filesDir.exists()) filesDir.mkdirs();
        File file = new File(filesDir.getPath()+"/" + name);
        if (create && !file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".Provider", file);
        return uri;
    }

    public static Uri getCacheUri(Context context, String name) {
        return getCacheUri(context, name, false);
    }

    public static Uri getCacheUri(Context context, String name, boolean create) {
        File cacheDir = context.getExternalCacheDir();
        if (cacheDir == null) cacheDir = context.getCacheDir();
        if (create && !cacheDir.exists()) cacheDir.mkdirs();
        File file = new File(cacheDir.getPath()+"/" + name);
        if (create && !file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Uri.fromFile(file);
    }

    public static Uri getProviderCacheUri(Context context, String name) {
        return getProviderCacheUri(context, name, false);
    }

    public static Uri getProviderCacheUri(Context context, String name, boolean create) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return getCacheUri(context, name, create);
        }
        File cacheDir = context.getExternalCacheDir();
        if (cacheDir == null) cacheDir = context.getCacheDir();
        if (create && !cacheDir.exists()) cacheDir.mkdirs();
        File file = new File(cacheDir.getPath()+"/" + name);
        if (create && !file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".Provider", file);
        return uri;
    }

    public static Uri getImageContentUri(Context context, File file) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return getUri(file);
        }
        String filePath = file.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID },
                MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (file.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    public static Uri getProviderUri(Context context, File file) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return getUri(file);
        }
        Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".Provider", file);
        return uri;
    }

    public static Uri getUri(File file) {
        return Uri.fromFile(file);
    }

    public static File handleImageOnKitKat(Context context, Uri uri) {
        String imagePath = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (DocumentsContract.isDocumentUri(context, uri)) {
                String docId = DocumentsContract.getDocumentId(uri);
                if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                    String id = docId.split(":")[1];
                    String selection = MediaStore.Images.Media._ID + "=" + id;
                    imagePath = getImagePath(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
                } else if ("com.android.provider.downloads.documents".equals(uri.getAuthority())) {
                    Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                    imagePath = getImagePath(context, contentUri, null);
                }
            } else if ("content".equalsIgnoreCase(uri.getScheme())) {
                imagePath = getImagePath(context, uri, null);
            } else if ("file".equalsIgnoreCase(uri.getScheme())) {
                imagePath = uri.getPath();
            }
            return new File(imagePath);
        } else {
            imagePath = getImagePath(context, uri, null);
            return new File(imagePath);
        }
    }

    private static String getImagePath(Context context, Uri uri, String selection) {
        String Path = null;
        Cursor cursor = context.getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                Path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return Path;
    }
}
