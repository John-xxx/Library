package com.liux.http.interceptor;

import com.liux.http.HttpUtil;
import com.liux.http.OnRequestListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

/**
 * 请求头/参数自定义拦截器
 * Created by Liux on 2017/11/29.
 */

public class CheckInterceptor implements Interceptor {
    private static final MediaType MEDIA_TYPE_TEXT = MediaType.parse("text/plain; charset=UTF-8");
    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=UTF-8");

    private OnRequestListener mOnRequestListener;

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request.Builder requestBuilder = request.newBuilder();

        try {
            /* 请求定制：自定义请求头 */
            if (mOnRequestListener != null) {
                checkHeader(request, requestBuilder);
            }
        } catch (Exception e) {
            requestBuilder.headers(request.headers());
            e.printStackTrace();
        }

        try {
            /* 请求体定制：自定义参数(针对文本型) */
            if (mOnRequestListener != null) {
                String method = request.method();
                if (HttpUtil.notRequiresRequestBody(method)) {
                    // 不允许有请求体的
                    checkQueryRequest(request, requestBuilder);
                } else {
                    // 允许有请求体的
                    checkBodyRequest(request, requestBuilder);
                }
            }
        } catch (Exception e) {
            requestBuilder.url(request.url()).method(request.method(), request.body());
            e.printStackTrace();
        }

        // 请求前
        Request newRequest = requestBuilder.tag(request.tag()).build();
        Response response = chain.proceed(newRequest);

        // 请求后
        int code = response.code();

        return response;
    }

    /**
     * 设置请求回调
     * @param listener
     */
    public void setOnCheckHeadersListener(OnRequestListener listener) {
        mOnRequestListener = listener;
    }

    /**
     * 检查请求头
     * @param request
     * @param requestBuilder
     */
    private void checkHeader(Request request, Request.Builder requestBuilder) {
        Map<String, String> headers = new IdentityHashMap<>();

        // 取出数据
        Headers oldHeaders = request.headers();
        for (String key : oldHeaders.names()) {
            for (String value : oldHeaders.values(key)) {
                headers.put(new String(key), value);
            }
        }

        mOnRequestListener.onHeaders(request, headers);

        // 合成新的 Header
        Headers.Builder builder = new Headers.Builder();
        for(Map.Entry<String, String> entry : headers.entrySet()){
            builder.add(
                    HttpUtil.checkChar(entry.getKey()),
                    HttpUtil.checkChar(entry.getValue())
            );
        }

        requestBuilder.headers(builder.build());
    }

    /**
     * 逆向解析 GET/HEAD 请求并检查参数
     * @param request
     * @return
     */
    private void checkQueryRequest(Request request, Request.Builder requestBuilder) {
        Map<String, String> params = new IdentityHashMap<>();

        // 获取原始参数
        Set<String> parameterNames = request.url().queryParameterNames();
        for (String key : parameterNames) {
            params.put(new String(key), request.url().queryParameter(key));
        }

        mOnRequestListener.onQueryRequest(request, params);

        // 恢复参数并组合成新的 HttpUrl
        HttpUrl url = request.url();
        HttpUrl.Builder builder = new HttpUrl.Builder()
                .scheme(url.scheme())
                .encodedUsername(url.username())
                .encodedPassword(url.password())
                .host(url.host())
                .port(url.port());
        for (String path : url.pathSegments()) {
            builder.addEncodedPathSegment(path);
        }
        for (Map.Entry<String, String> param : params.entrySet()) {
            builder.addEncodedQueryParameter(param.getKey(), param.getValue());
        }
        builder.encodedFragment(url.fragment());

        requestBuilder.url(builder.build()).method(request.method(), null);
    }

    /**
     * 逆向解析 POST/DELETE/PUT/PATCH 请求并检查参数(目前只处理文本型参数)
     * @param request
     * @param requestBuilder
     */
    private void checkBodyRequest(Request request, Request.Builder requestBuilder) throws IOException {
        RequestBody requestBody = request.body();
        RequestBody newRequestBody;
        if (requestBody instanceof MultipartBody) {
            newRequestBody = checkMultipartBodyParams(request, (MultipartBody) requestBody);
        } else if (requestBody instanceof FormBody) {
            newRequestBody = checkFormBodyParams(request, (FormBody) requestBody);
        } else {
            newRequestBody = checkRequestBodyParams(request, requestBody);
        }
        requestBuilder.url(request.url()).method(request.method(), newRequestBody);
    }

    private RequestBody checkMultipartBodyParams(Request request, MultipartBody multipartBody) throws IOException {
        Map<String, String> params = new IdentityHashMap<>();
        List<MultipartBody.Part> oldParts = new ArrayList<>();

        // 读取原始参数
        for (MultipartBody.Part part : multipartBody.parts()) {
            Headers head  = part.headers();
            RequestBody body  = part.body();
            MediaType type = body.contentType();
            if (MEDIA_TYPE_TEXT.equals(type) || MEDIA_TYPE_JSON.equals(type)) {
                Buffer buffer = new Buffer();
                body.writeTo(buffer);
                String key = head.value(0).substring(head.value(0).lastIndexOf("=") + 1).replace("\"", "");
                String value = HttpUtil.json2String(buffer.readUtf8());
                params.put(new String(key), value);
            } else {
                oldParts.add(part);
            }
        }

        mOnRequestListener.onBodyRequest(request, params);

        // 恢复参数并组合成新的 MultipartBody
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(multipartBody.type());
        for (MultipartBody.Part part : oldParts) {
            builder.addPart(part);
        }
        for (Map.Entry<String, String> entry : params.entrySet()) {
            builder.addFormDataPart(entry.getKey(), entry.getValue());
        }
        return builder.build();
    }

    private RequestBody checkFormBodyParams(Request request, FormBody formBody) throws IOException {
        Map<String, String> params = new IdentityHashMap<>();

        // 读取原始参数
        for (int i = 0; i < formBody.size(); i++) {
            params.put(new String(formBody.name(i)), formBody.value(i));
        }

        mOnRequestListener.onBodyRequest(request, params);

        // 组合成新的  FormBody
        FormBody.Builder builder = new FormBody.Builder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            builder.addEncoded(entry.getKey(), entry.getValue());
        }
        return builder.build();
    }

    private RequestBody checkRequestBodyParams(Request request, RequestBody requestBody) throws IOException {
        String param = null;

        // 读取原始参数
        Buffer buffer = new Buffer();
        requestBody.writeTo(buffer);
        param = buffer.readUtf8();

        mOnRequestListener.onBodyRequest(request, param);

        // 组合成新的 RequestBody
        return RequestBody.create(requestBody.contentType(), param.getBytes());
    }
}
