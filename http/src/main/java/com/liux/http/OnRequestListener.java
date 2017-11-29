package com.liux.http;

import java.util.Map;

import okhttp3.Request;

/**
 * 请求动作监听器
 * Created by Liux on 2017/11/29.
 */

public interface OnRequestListener {

    /**
     * Header 回调, 支持一个 key 对应多个 value <br>
     * headers.put(key, value); 覆盖
     * headers.put(new String(key), value); 增加
     * @param request
     * @param headers
     */
    void onHeaders(Request request, Map<String, String> headers);

    /**
     * GET/HEAD 请求回调,支持一个 key 对应多个 value <br>
     * queryParams.put(key, value); 覆盖
     * queryParams.put(new String(key), value); 增加
     * @param request
     * @param queryParams
     */
    void onQueryRequest(Request request, Map<String, String> queryParams);

    /**
     * POST/DELETE/PUT/PATCH 请求回调,支持一个 key 对应多个 value <br>
     * bodyParams.put(key, value); 覆盖
     * bodyParams.put(new String(key), value); 增加
     * @param request
     * @param bodyParams
     */
    void onBodyRequest(Request request, Map<String, String> bodyParams);

    /**
     * POST/DELETE/PUT/PATCH 请求回调,纯文本形式
     * @param request
     * @param param
     */
    void onBodyRequest(Request request, String param);
}
