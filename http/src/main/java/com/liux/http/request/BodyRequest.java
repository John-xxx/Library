package com.liux.http.request;

import com.liux.http.HttpUtil;
import com.liux.http.progress.OnProgressListener;
import com.liux.http.progress.OnRequestProgressListener;
import com.liux.http.progress.OnResponseProgressListener;
import com.liux.http.progress.RequestProgressBody;

import java.io.File;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;

import okhttp3.*;
import okhttp3.Request;
import okio.ByteString;

/**
 * Created by Liux on 2018/2/26.
 */

public class BodyRequest<T extends BodyRequest> extends QueryRequest<T> {
    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_FROM = 1;

    private int mType = TYPE_NORMAL;

    private String mBodyType;
    private Object mBodyObject;

    private boolean mIsMultipart = false;
    private IdentityHashMap<String, Object> mBodyHashMap;

    private OnRequestProgressListener mOnRequestProgressListener;

    public BodyRequest(Call.Factory factory, Method method) {
        super(factory, method);
    }

    @Override
    public T url(String url) {
        return super.url(url);
    }

    @Override
    public T header(String name, String value) {
        return super.header(name, value);
    }

    @Override
    public T addHeader(String name, String value) {
        return super.addHeader(name, value);
    }

    @Override
    public T removeHeader(String name) {
        return super.removeHeader(name);
    }

    @Override
    public T tag(Object object) {
        return super.tag(object);
    }

    @Override
    protected Request.Builder onCreateRequestBuilder(Request.Builder builder) {
        return builder.method(getMethod().toString(), getRequestBody());
    }

    @Override
    protected Request onCreateRequest(Request request) {
        if (mOnRequestProgressListener != null) {
            RequestBody requestBody = request.body();
            if (requestBody != null) {
                requestBody = new RequestProgressBody(request.url(), request.body(), mOnRequestProgressListener);
            }
            request = request.newBuilder()
                    .method(request.method(), requestBody)
                    .build();
        }
        return request;
    }

    @Override
    public T query(String name, String value) {
        return super.query(name, value);
    }

    @Override
    public T addQuery(String name, String value) {
        return super.addQuery(name, value);
    }

    @Override
    public T removeQuery(String name) {
        return super.removeQuery(name);
    }

    @Override
    public T removeQueryAll(String name) {
        return super.removeQueryAll(name);
    }

    public T body(String type, String string) {
        bodyObject(type, string);
        return (T) this;
    }

    public T body(String type, ByteString byteString) {
        bodyObject(type, byteString);
        return (T) this;
    }

    public T body(String type, byte[] bytes) {
        bodyObject(type, bytes);
        return (T) this;
    }

    public T body(String type, File file) {
        bodyObject(type, file);
        return (T) this;
    }

    public T from() {
        mIsMultipart = false;
        return (T) this;
    }

    public T multipart() {
        mIsMultipart = true;
        return (T) this;
    }

    public T param(String name, String string) {
        paramObject(name, string);
        return (T) this;
    }

    public T param(String name, File file) {
        return param(name, file, file.getName());
    }

    public T param(String name, File file, String fileName) {
        paramObject(name, HttpUtil.parseFilePart(name, file, fileName));
        return (T) this;
    }

    public T addParam(String name, String string) {
        addParamObject(name, string);
        return (T) this;
    }

    public T addParam(String name, File file) {
        return addParam(name, file, file.getName());
    }

    public T addParam(String name, File file, String fileName) {
        if (!mIsMultipart) mIsMultipart = true;
        addParamObject(name, HttpUtil.parseFilePart(name, file, fileName));
        return (T) this;
    }

    public T removeParam(String name) {
        getBodyHashMap().remove(name);
        return (T) this;
    }

    public T removeParamAll(String name) {
        for (Iterator<Map.Entry<String, Object>> it = getBodyHashMap().entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, Object> entry = it.next();
            if (entry.getKey().equals(name)) {
                it.remove();
            }
        }
        return (T) this;
    }

    public T progress(OnProgressListener listener) {
        progress((OnRequestProgressListener) listener);
        progress((OnResponseProgressListener) listener);
        return (T) this;
    }

    public T progress(OnRequestProgressListener listener) {
        mOnRequestProgressListener = listener;
        return (T) this;
    }

    protected IdentityHashMap<String, Object> getBodyHashMap() {
        if (mBodyHashMap == null) {
            mBodyHashMap = new IdentityHashMap<>();
        }
        return mBodyHashMap;
    }

    private void bodyObject(String type, Object object) {
        mType = TYPE_NORMAL;
        mBodyType = type;
        mBodyObject = object;
    }

    private void paramObject(String name, Object object) {
        mType = TYPE_FROM;
        getBodyHashMap().put(name, object);
    }

    private void addParamObject(String name, Object object) {
        mType = TYPE_FROM;
        getBodyHashMap().put(new String(name), object);
    }

    private RequestBody getRequestBody() {
        RequestBody requestBody;
        switch (mType) {
            case TYPE_NORMAL:
                requestBody = onCreateRequestBody();
                break;
            case TYPE_FROM:
                if (!mIsMultipart) {
                    requestBody = onCreateFormBody();
                } else {
                    requestBody = onCreateMultipartBody();
                }
                break;
            default:
                requestBody = null;
                break;
        }
        return requestBody;
    }

    private RequestBody onCreateRequestBody() {
        if (mBodyObject instanceof String) {
            return RequestBody.create(MediaType.parse(mBodyType), (String) mBodyObject);
        } else if (mBodyObject instanceof ByteString) {
            return RequestBody.create(MediaType.parse(mBodyType), (ByteString) mBodyObject);
        } else if (mBodyObject instanceof byte[]) {
            return RequestBody.create(MediaType.parse(mBodyType), (byte[]) mBodyObject);
        } else if (mBodyObject instanceof File) {
            return RequestBody.create(MediaType.parse(mBodyType), (File) mBodyObject);
        }
        return null;
    }

    private RequestBody onCreateFormBody() {
        FormBody.Builder builder = new FormBody.Builder();
        for (Map.Entry<String, Object> entry : getBodyHashMap().entrySet()) {
            String name = entry.getKey();
            Object value = entry.getValue();
            if (name != null && value != null) {
                if (value instanceof String) {
                    builder.addEncoded(name, (String) value);
                } else {
                    builder.addEncoded(name, value.toString());
                }
            }
        }
        return builder.build();
    }

    private RequestBody onCreateMultipartBody() {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        for (Map.Entry<String, Object> entry : getBodyHashMap().entrySet()) {
            String name = entry.getKey();
            Object value = entry.getValue();
            if (name != null && value != null) {
                if (value instanceof String){
                    builder.addFormDataPart(name, (String) value);
                } else if (value instanceof MultipartBody.Part) {
                    builder.addPart((MultipartBody.Part) value);
                } else {
                    builder.addFormDataPart(name, value.toString());
                }
            }
        }
        return builder.build();
    }

    public static class Method extends QueryRequest.Method {

        public static final Method POST = new Method("POST");
        public static final Method PUT = new Method("PUT");
        public static final Method PATCH = new Method("PATCH");
        public static final Method DELETE = new Method("DELETE");

        Method(String method) {
            super(method);
        }
    }
}