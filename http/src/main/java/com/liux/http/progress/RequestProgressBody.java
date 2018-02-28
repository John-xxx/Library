package com.liux.http.progress;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
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

public class RequestProgressBody extends RequestBody {

    private HttpUrl mHttpUrl;
    private RequestBody mRequestBody;
    private OnRequestProgressListener mOnRequestProgressListener;

    private BufferedSink mWrapperBufferedSink;

    public RequestProgressBody(HttpUrl httpUrl, RequestBody requestBody, OnRequestProgressListener onRequestProgressListener) {
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
//        if (mWrapperBufferedSink == null) {
//            mWrapperBufferedSink = Okio.buffer(new WrapperForwardingSink(sink, mRequestBody, mOnRequestProgressListener));
//        }
        mWrapperBufferedSink = Okio.buffer(new WrapperForwardingSink(sink, mHttpUrl, mRequestBody, mOnRequestProgressListener));
        // 写入
        mRequestBody.writeTo(mWrapperBufferedSink);
        // 必须调用flush，否则最后一部分数据可能不会被写入
        mWrapperBufferedSink.flush();
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
            mTotalBytesWrite += byteCount;

            //回调
            mOnRequestProgressListener.onRequestProgress(mHttpUrl, mTotalBytesWrite, mContentLength, mTotalBytesWrite == mContentLength);
        }
    }
}