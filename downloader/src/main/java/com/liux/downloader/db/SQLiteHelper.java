package com.liux.downloader.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Liux on 2017/12/10.
 */

public class SQLiteHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "downloader";

    private static final int DB_VERSION_10 = 10;
    private static final int DB_VERSION_20 = 20;
    private static final int DB_VERSION_30 = 30;

    public SQLiteHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION_10);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE 'record' (" +
                        // 唯一ID
                        "'id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                        // Url 地址
                        "'url'  TEXT," +
                        // 请求头
                        "'header'  TEXT," +
                        // 存储目录
                        "'dir'  TEXT," +
                        // 存储名称
                        "'name'  TEXT," +
                        // 当前已下载
                        "'sofar'  INTEGER," +
                        // 下载总量
                        "'total'  INTEGER," +
                        // 缓存标识
                        "'etag'  TEXT," +
                        // 任务状态
                        "'status'  INTEGER," +
                        // 更新时间
                        "'update'  INTEGER);"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case DB_VERSION_10:
                // 阶段升级代码
            case DB_VERSION_20:
                // 阶段升级代码
            case DB_VERSION_30:
                // 阶段升级代码
                break;
            default:
                break;
        }
    }
}
