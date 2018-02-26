package com.liux.http.request;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by Liux on 2018/2/26.
 */

public class BodyRequest<T extends Request> extends QueryRequest<T> {
    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_FROM = 1;
    private static final int TYPE_MULTIPART = 2;

    private int mType = TYPE_NORMAL;

    public BodyRequest(Call.Factory factory, String method) {
        super(factory, method);
    }

    @Override
    protected okhttp3.Request.Builder onCreateRequestBuilder() {
        return new okhttp3.Request.Builder().method(getMethod(), getRequestBody());
    }

    private RequestBody getRequestBody() {
        RequestBody requestBody;
        switch (mType) {
            case TYPE_NORMAL:
                requestBody = createRequestBody();
                break;
            case TYPE_FROM:
                requestBody = createFormBody();
                break;
            case TYPE_MULTIPART:
                requestBody = createMultipartBody();
                break;
        }
        return null;
    }

    private RequestBody createRequestBody() {
//        RequestBody.create();
        return RequestBody.create(null, "");
    }

    private RequestBody createFormBody() {
        FormBody.Builder builder = new FormBody.Builder();
//        if (param != null) {
//            for (Map.Entry<String, String> entry : param.entrySet()) {
//                String key = entry.getKey();
//                String value = entry.getValue();
//                if (key != null && value != null) {
//                    builder.addEncoded(key, value);
//                }
//            }
//        }
        return builder.build();
    }

    private RequestBody createMultipartBody() {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
//        if (param != null) {
//            for (Map.Entry<String, Object> entry : param.entrySet()) {
//                String key = entry.getKey();
//                Object value = entry.getValue();
//                if (key != null && value != null) {
//                    if (value instanceof File) {
//                        builder.addPart(HttpUtil.parsePart(key, (File) value));
//                    } else {
//                        builder.addFormDataPart(key, String.valueOf(value));
//                    }
//                }
//            }
//        }
        return builder.build();
    }
}
