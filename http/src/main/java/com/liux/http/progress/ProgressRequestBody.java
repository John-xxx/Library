package com.liux.http.progress;

import com.liux.http.wrapper.WrapperRequestBody;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;

/**
 * 2018/2/27
 * By Liux
 * lx0758@qq.com
 */

public class ProgressRequestBody extends RequestBody implements WrapperRequestBody {

    private HttpUrl mHttpUrl;
    private RequestBody mRequestBody;
    private OnRequestProgressListener mOnRequestProgressListener;

    private BufferedSink mWrapperBufferedSink;

    public ProgressRequestBody(HttpUrl httpUrl, RequestBody requestBody, OnRequestProgressListener onRequestProgressListener) {
        mHttpUrl = httpUrl;
        mRequestBody = requestBody;
        mOnRequestProgressListener = onRequestProgressListener;
    }

    @Override
    public MediaType contentType() {
        return mRequestBody.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        return mRequestBody.contentLength();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        if (mWrapperBufferedSink == null) {
            mWrapperBufferedSink = Okio.buffer(new WrapperForwardingSink(sink, mHttpUrl, mRequestBody, mOnRequestProgressListener));
            mRequestBody.writeTo(mWrapperBufferedSink);
            mWrapperBufferedSink.flush();
        } else {
            mRequestBody.writeTo(sink);
        }
    }

    @Override
    public boolean isChildWarpper() {
        return mRequestBody instanceof WrapperRequestBody;
    }

    @Override
    public boolean isFormBody() {
        if (isChildWarpper()) {
            return ((WrapperRequestBody) mRequestBody).isFormBody();
        }
        return mRequestBody instanceof FormBody;
    }

    @Override
    public boolean isMultipartBody() {
        if (isChildWarpper()) {
            return ((WrapperRequestBody) mRequestBody).isMultipartBody();
        }
        return mRequestBody instanceof MultipartBody;
    }

    @Override
    public RequestBody getRequestBody() {
        if (isChildWarpper()) {
            return ((WrapperRequestBody) mRequestBody).getRequestBody();
        }
        return mRequestBody;
    }

    @Override
    public void setRequestBody(RequestBody requestBody) {
        if (isChildWarpper()) {
            ((WrapperRequestBody) mRequestBody).setRequestBody(requestBody);
            return;
        }
        mRequestBody = requestBody;
    }

    private static class WrapperForwardingSink extends ForwardingSink {

        private HttpUrl mHttpUrl;
        private RequestBody mRequestBody;
        private OnRequestProgressListener mOnRequestProgressListener;

        private long mContentLength = 0L;
        private long mTotalBytesWrite = 0L;

        public WrapperForwardingSink(BufferedSink sink, HttpUrl httpUrl, RequestBody requestBody, OnRequestProgressListener onRequestProgressListener) {
            super(sink);
            mHttpUrl = httpUrl;
            mRequestBody = requestBody;
            mOnRequestProgressListener = onRequestProgressListener;
        }

        @Override
        public void write(Buffer source, long byteCount) throws IOException {
            super.write(source, byteCount);

            if (mContentLength == 0) {
                mContentLength = mRequestBody.contentLength();
            }

            //增加当前写入的字节数
            long totalBytesWrite = mTotalBytesWrite + byteCount;

            //回调
            if (totalBytesWrite == 0 || totalBytesWrite != mTotalBytesWrite) {
                mTotalBytesWrite = totalBytesWrite;
                mOnRequestProgressListener.onRequestProgress(mHttpUrl, mTotalBytesWrite, mContentLength, mTotalBytesWrite == mContentLength);
            }
        }
    }
}
