package com.liux.http.wrapper;

import okhttp3.RequestBody;

/**
 * 2018/4/9
 * By Liux
 * lx0758@qq.com
 */
public interface WrapperRequestBody {

    boolean isChildWarpper();

    boolean isFormBody();

    boolean isMultipartBody();

    RequestBody getRequestBody();

    void setRequestBody(RequestBody requestBody);
}
