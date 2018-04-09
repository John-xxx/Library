package com.liux.http.wrapper;

import okhttp3.FormBody;
import okhttp3.MultipartBody;

/**
 * 2018/4/9
 * By Liux
 * lx0758@qq.com
 */
public interface WrapperRequestBody {

    boolean isMultipartBody();

    MultipartBody getMultipartBody();

    boolean isFormBody();

    FormBody getFormBody();
}
