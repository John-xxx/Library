package com.liux.http.request;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 2018/4/10
 * By Liux
 * lx0758@qq.com
 */
public abstract class Result {

    protected void onFailure(Call call, IOException e) {
        onFailure(e);
    }

    protected void onResponse(Call call, Response response) throws IOException {
        onResponse(response);
    }

    protected abstract void onFailure(IOException e);

    protected abstract void onResponse(Response response) throws IOException;
}
