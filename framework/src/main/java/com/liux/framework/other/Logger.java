package com.liux.framework.other;

import android.util.Log;

/**
 * Created by Liux on 2017/9/13.
 */

public class Logger {

    private static boolean DEBUG = true;

    public static int v(String tag, String msg) {
        if (!DEBUG) return -1;
        return Log.v(tag, msg);
    }

    public static int v(String tag, String msg, Throwable tr) {
        if (!DEBUG) return -1;
        return Log.v(tag, msg, tr);
    }

    public static int d(String tag, String msg) {
        if (!DEBUG) return -1;
        return Log.d(tag, msg);
    }

    public static int d(String tag, String msg, Throwable tr) {
        if (!DEBUG) return -1;
        return Log.d(tag, msg, tr);
    }

    public static int i(String tag, String msg) {
        if (!DEBUG) return -1;
        return Log.i(tag, msg);
    }

    public static int i(String tag, String msg, Throwable tr) {
        if (!DEBUG) return -1;
        return Log.i(tag, msg, tr);
    }

    public static int w(String tag, String msg) {
        if (!DEBUG) return -1;
        return Log.w(tag, msg);
    }

    public static int w(String tag, String msg, Throwable tr) {
        if (!DEBUG) return -1;
        return Log.w(tag, msg, tr);
    }

    public static int e(String tag, String msg) {
        if (!DEBUG) return -1;
        return Log.e(tag, msg);
    }

    public static int e(String tag, String msg, Throwable tr) {
        if (!DEBUG) return -1;
        return Log.e(tag, msg, tr);
    }
}
