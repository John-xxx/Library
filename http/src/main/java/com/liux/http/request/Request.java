package com.liux.http.request;

import android.text.TextUtils;

import com.liux.http.interceptor.TimeoutInterceptor;

import java.io.IOException;
import java.util.IdentityHashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Response;

/**
 * Created by Liux on 2018/2/26.
 */

public abstract class Request<T extends Request> {

    private static final String HEADER_REQUEST_NAME = "Request-From";
    private static final String HEADER_REQUEST_VALUE = "Http";

    public static boolean isManuallyRequest(okhttp3.Request request) {
        String from = request.header(HEADER_REQUEST_NAME);
        return !TextUtils.isEmpty(from) && HEADER_REQUEST_VALUE.equals(from);
    }

    private String mUrl;
    private Object mTag;
    private Method mMethod;

    private Call mCall;
    private Call.Factory mFactory;

    private IdentityHashMap<String, String> mHeaderHashMap;

    public Request(Call.Factory factory, Method method) {
        mFactory = factory;
        mMethod = method;

        distinguishRequest(true);
    }

    public T url(String url) {
        mUrl = url;
        return (T) this;
    }

    public T header(String name, String value) {
        getHeaderHashMap().put(name, value);
        return (T) this;
    }

    public T addHeader(String name, String value) {
        getHeaderHashMap().put(new String(name), value);
        return (T) this;
    }

    public T removeHeader(String name) {
        getHeaderHashMap().remove(name);
        return (T) this;
    }

    public T connectTimeout(int second) {
        if (second > 0) {
            getHeaderHashMap().put(TimeoutInterceptor.HEADER_TIMEOUT_CONNECT, String.valueOf(second));
        } else {
            getHeaderHashMap().remove(TimeoutInterceptor.HEADER_TIMEOUT_CONNECT);
        }
        return (T) this;
    }

    public T writeTimeout(int second) {
        if (second > 0) {
            getHeaderHashMap().put(TimeoutInterceptor.HEADER_TIMEOUT_WRITE, String.valueOf(second));
        } else {
            getHeaderHashMap().remove(TimeoutInterceptor.HEADER_TIMEOUT_WRITE);
        }
        return (T) this;
    }

    public T readTimeout(int second) {
        if (second > 0) {
            getHeaderHashMap().put(TimeoutInterceptor.HEADER_TIMEOUT_READ, String.valueOf(second));
        } else {
            getHeaderHashMap().remove(TimeoutInterceptor.HEADER_TIMEOUT_READ);
        }
        return (T) this;
    }

    public T distinguishRequest(boolean distinguish) {
        if (distinguish) {
            getHeaderHashMap().put(HEADER_REQUEST_NAME, HEADER_REQUEST_VALUE);
        } else {
            getHeaderHashMap().remove(HEADER_REQUEST_NAME);
        }
        return (T) this;
    }

    public T tag(Object object) {
        mTag = object;
        return (T) this;
    }

    public Response sync() throws IOException {
        checkUrl();
        Response response = handlerCall().execute();
        response = handlerResponse(response);
        return response;
    }

    public void async() {
        async(null);
    }

    public void async(final Callback callback) {
        checkUrl();
        handlerCall().enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                if (callback != null) callback.onFailure(call, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                response = handlerResponse(response);
                if (callback != null) callback.onResponse(call, response);
            }
        });
    }

    public void cancel() {
        Call call = getCall();
        if (call != null && !call.isCanceled() && !call.isExecuted()) {
            call.cancel();
        }
    }

    protected abstract HttpUrl.Builder onCreateHttpUrlBuilder(HttpUrl.Builder builder);

    protected abstract HttpUrl onCreateHttpUrl(HttpUrl httpUrl);

    protected abstract okhttp3.Request.Builder onCreateRequestBuilder(okhttp3.Request.Builder builder);

    protected abstract okhttp3.Request onCreateRequest(okhttp3.Request request);

    protected abstract okhttp3.Response.Builder onCreateResponseBuilder(okhttp3.Response.Builder builder);

    protected abstract okhttp3.Response onCreateResponse(okhttp3.Response response);

    protected HttpUrl handlerHttpUrl() {
        HttpUrl.Builder builder = HttpUrl.parse(getUrl()).newBuilder();
        builder = onCreateHttpUrlBuilder(builder);

        HttpUrl httpUrl = builder.build();
        httpUrl = onCreateHttpUrl(httpUrl);

        return httpUrl;
    }

    protected Call handlerCall() {
        HttpUrl httpUrl = handlerHttpUrl();

        okhttp3.Request.Builder builder = new okhttp3.Request.Builder();
        builder = onCreateRequestBuilder(builder);

         builder.url(httpUrl).tag(getTag()).headers(Headers.of(getHeaderHashMap()));

        okhttp3.Request request = builder.build();
        request = onCreateRequest(request);

        mCall = getFactory().newCall(request);
        return mCall;
    }

    protected Response handlerResponse(Response response) {
        Response.Builder builder = onCreateResponseBuilder(response.newBuilder());
        response = onCreateResponse(builder.build());
        return response;
    }

    protected String getUrl() {
        return mUrl;
    }

    protected Object getTag() {
        return mTag;
    }

    protected Method getMethod() {
        return mMethod;
    }

    protected Call getCall() {
        return mCall;
    }

    protected Call.Factory getFactory() {
        return mFactory;
    }

    protected IdentityHashMap<String, String> getHeaderHashMap() {
        if (mHeaderHashMap == null) {
            mHeaderHashMap = new IdentityHashMap<>();
        }
        return mHeaderHashMap;
    }

    private void checkUrl() {
        if (TextUtils.isEmpty(getUrl())) {
            throw new NullPointerException("url is empty");
        }
        if (HttpUrl.parse(getUrl()) == null) {
            throw new IllegalArgumentException("\"" + getUrl() + "\" is not right");
        }
    }
}
