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
    private static String USER_AGENT = System.getProperty("http.agent");

    public UserAgentInterceptor(Context context) {
        USER_AGENT = HttpUtil.getDefaultUserAgent(context);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        // 手动添加该请求头时OkHttp不会自动解压响应数据
        // builder.header("Accept-Encoding", "gzip,deflate");
        builder.header("User-Agent", HttpUtil.checkChar(USER_AGENT));
        return chain.proceed(builder.build());
    }

    public String getUserAgent() {
        return USER_AGENT;
    }

    public void setUserAgent(String userAgent) {
        USER_AGENT = userAgent;
    }
}
