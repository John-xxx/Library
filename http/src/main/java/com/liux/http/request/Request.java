package com.liux.http.request;

import com.liux.http.HttpUtil;

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

    private String mUrl;
    private Object mTag;
    private String mMethod;

    private Call mCall;
    private Call.Factory mFactory;
    private IdentityHashMap<String, String> mHeaderHashMap;

    public Request(Call.Factory factory, String method) {
        if (!HttpUtil.isHttpMethod(method)) throw new IllegalArgumentException("method is not right");
        mFactory = factory;
        mMethod = method;
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

    public T tag(Object object) {
        mTag = object;
        return (T) this;
    }

    public void async(Callback callback) {
        onCreateCall().enqueue(callback);
    }

    public Response sync() throws IOException {
        return onCreateCall().execute();
    }

    protected abstract HttpUrl.Builder onCreateHttpUrlBuilder();

    protected abstract okhttp3.Request.Builder onCreateRequestBuilder();

    protected String getUrl() {
        return mUrl;
    }

    protected Object getTag() {
        return mTag;
    }

    protected String getMethod() {
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

    private Call onCreateCall() {
        okhttp3.Request.Builder builder = onCreateRequestBuilder()
                .url(onCreateHttpUrlBuilder().build())
                .tag(getTag())
                .headers(Headers.of(getHeaderHashMap()));

        mCall = getFactory().newCall(builder.build());

        return mCall;
    }
}
