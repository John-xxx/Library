package com.liux.example;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.liux.http.Http;
import com.liux.http.OnHeaderListener;
import com.liux.http.OnRequestListener;
import com.liux.http.interceptor.HttpLoggingInterceptor;
import com.liux.rx.lifecycle.LifecyleProviderManager;

import java.util.Map;

import okhttp3.Request;

/**
 * Created by Liux on 2017/8/16.
 */

public class ApplocationInstance extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
        LifecyleProviderManager.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        /* 初始化 Http */
        Http.init(this, "http://6xyun.cn/api/");
        Http.get().setLoggingLevel(HttpLoggingInterceptor.Level.BODY);
        Http.get().setOnHeaderListener(new OnHeaderListener() {
            @Override
            public void onHeaders(Request request, Map<String, String> headers) {

            }
        });
        Http.get().setOnRequestListener(new OnRequestListener() {
            @Override
            public void onQueryRequest(Request request, Map<String, String> queryParams) {

            }

            @Override
            public void onBodyRequest(Request request, Map<String, String> bodyParams) {

            }

            @Override
            public void onBodyRequest(Request request, String param) {

            }
        });
    }
}
