package com.liux.http.request;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 2018/4/10
 * By Liux
 * lx0758@qq.com
 */
public abstract class UIResult extends Result {

    private MainHandler mMainHandler = new MainHandler(this);

    @Override
    protected void onFailure(Call call, IOException e) {
        mMainHandler.onFailure(e);
    }

    @Override
    protected void onResponse(Call call, Response response) throws IOException {
        mMainHandler.onResponse(response);
    }

    protected void onMainFailure(IOException e) {
        onFailure(e);
    }

    protected void onMainResponse(Response response) throws IOException {
        onResponse(response);
    }

    private static class MainHandler extends Handler {
        private static final int MSG_WHAT_FAILURE = 1;
        private static final int MSG_WHAT_RESPONSE = 2;

        private UIResult mUIResult;

        MainHandler(UIResult uiResult) {
            super(Looper.getMainLooper());
            mUIResult = uiResult;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_WHAT_FAILURE:
                    mUIResult.onMainFailure((IOException) msg.obj);
                    break;
                case MSG_WHAT_RESPONSE:
                    try {
                        mUIResult.onMainResponse((Response) msg.obj);
                    } catch (IOException e) {
                        mUIResult.onFailure(e);
                    }
                    break;
            }
        }

        void onFailure(IOException e) {
            Message msg = Message.obtain();
            msg.what = MSG_WHAT_FAILURE;
            msg.obj = e;
            sendMessage(msg);
        }

        void onResponse(Response response) {
            Message msg = Message.obtain();
            msg.what = MSG_WHAT_RESPONSE;
            msg.obj = response;
            sendMessage(msg);
        }
    }
}
