package com.liux.http.request;

import java.util.IdentityHashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.HttpUrl;

/**
 * Created by Liux on 2018/2/26.
 */

public class QueryRequest<T extends Request> extends Request<T> {

    private IdentityHashMap<String, String> mQueryHashMap;

    public QueryRequest(Call.Factory factory, String method) {
        super(factory, method);
    }

    @Override
    protected HttpUrl.Builder onCreateHttpUrlBuilder() {
        HttpUrl.Builder builder = HttpUrl.parse(getUrl()).newBuilder();

        for (Map.Entry<String, String> param : getQueryHashMap().entrySet()) {
            builder.addEncodedQueryParameter(param.getKey(), param.getValue());
        }

        return builder;
    }

    @Override
    protected okhttp3.Request.Builder onCreateRequestBuilder() {
        return new okhttp3.Request.Builder().method(getMethod(), null);
    }

    public T query(String name, String value) {
        getQueryHashMap().put(name, value);
        return (T) this;
    }

    public T addQuery(String name, String value) {
        getQueryHashMap().put(new String(name), value);
        return (T) this;
    }

    public T removeQuery(String name) {
        getQueryHashMap().remove(name);
        return (T) this;
    }

    protected IdentityHashMap<String, String> getQueryHashMap() {
        if (mQueryHashMap == null) {
            mQueryHashMap = new IdentityHashMap<>();
        }
        return mQueryHashMap;
    }
}
