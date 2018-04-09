package com.liux.http.wrapper;

import okhttp3.ResponseBody;

/**
 * 2018/4/9
 * By Liux
 * lx0758@qq.com
 */
public interface WrapperResponseBody {

    boolean isResponseBody();

    ResponseBody getResponseBody();
}
