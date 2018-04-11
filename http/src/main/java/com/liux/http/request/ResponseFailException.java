package com.liux.http.request;

import java.net.UnknownServiceException;

import okhttp3.Response;

/**
 * 2018/4/11
 * By Liux
 * lx0758@qq.com
 */
public class ResponseFailException extends UnknownServiceException {

    private Response mResponse;

    public ResponseFailException() {
    }

    public ResponseFailException(String msg) {
        super(msg);
    }

    public ResponseFailException(Response response) {
        super(response.message());
        mResponse = response;
    }

    public Response getResponse() {
        return mResponse;
    }

    @Override
    public String toString() {
        String s = getClass().getSimpleName();
        String message = getLocalizedMessage();
        return (message != null) ? (s + ": " + message) : s;
    }
}
