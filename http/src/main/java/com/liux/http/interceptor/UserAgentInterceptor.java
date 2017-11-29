package com.liux.http.interceptor;

import android.content.Context;

import com.liux.http.HttpUtil;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Liux on 2017/11/29.
 */

public class UserAgentInterceptor implements Interceptor {
    private static String mUserAgent = System.getProperty("http.agent");

    public UserAgentInterceptor(Context context) {
        mUserAgent = HttpUtil.getDefaultUserAgent(context);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        // 手动添加该请求头时OkHttp不会自动解压响应数据
        // builder.header("Accept-Encoding", "gzip,deflate");
        builder.header("User-Agent", HttpUtil.checkChar(mUserAgent));
        return chain.proceed(builder.build());
    }

    public static String getUserAgent() {
        return mUserAgent;
    }
}
