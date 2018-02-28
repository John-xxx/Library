package com.liux.http.progress;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;

/**
 * 2018/2/27
 * By Liux
 * lx0758@qq.com
 */

public class ResponseProgressBody extends ResponseBody {

    private HttpUrl mHttpUrl;
    private ResponseBody mResponseBody;
    private OnResponseProgressListener mResponseProgressListener;

    private BufferedSource mWrapperBufferedSource;
    
    public ResponseProgressBody(HttpUrl httpUrl, ResponseBody responseBody, OnResponseProgressListener onResponseProgressListener) {
        mHttpUrl = httpUrl;
        mResponseBody = responseBody;
        mResponseProgressListener = onResponseProgressListener;
    }

    @Override
    public MediaType contentType() {
        return mResponseBody.contentType();
    }

    @Override
    public long contentLength() {
        return mResponseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (mWrapperBufferedSource == null) {
            mWrapperBufferedSource = Okio.buffer(new WrapperForwardingSource(mHttpUrl, mResponseBody, mResponseProgressListener));
        }
        return mWrapperBufferedSource;
    }

    private static class WrapperForwardingSource extends ForwardingSource {

        private HttpUrl mHttpUrl;
        private ResponseBody mResponseBody;
        private OnResponseProgressListener mResponseProgressListener;

        private long mContentLength = 0L;
        private long mTotalBytesRead = 0L;

        public WrapperForwardingSource(HttpUrl httpUrl, ResponseBody responseBody, OnResponseProgressListener onResponseProgressListener) {
            super(responseBody.source());
            mHttpUrl = httpUrl;
            mResponseBody = responseBody;
            mResponseProgressListener = onResponseProgressListener;
        }

        @Override
        public long read(Buffer sink, long byteCount) throws IOException {
            long bytesRead = super.read(sink, byteCount);

            if (mContentLength == 0) {
                mContentLength = mResponseBody.contentLength();
            }

            //增加当前读取的字节数，如果读取完成了bytesRead会返回-1
            mTotalBytesRead += bytesRead != -1 ? bytesRead : 0;

            //回调，如果contentLength()不知道长度，会返回-1
            mResponseProgressListener.onResponseProgress(mHttpUrl, mTotalBytesRead, mContentLength, bytesRead == -1);

            return bytesRead;
        }
    }
}