package com.liux.framework.http;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 基于 OkHttp3 的简单下载器实现
 * Created by Liux on 2017/9/3.
 */

public class Downloader {
    private File mFile;
    private Request.Builder mRequest;
    private OnDownloadListener mOnDownloadListener;

    public Downloader() {
        mRequest = new Request.Builder();
    }

    public Downloader url(String url) {
        mRequest.url(url);
        return this;
    }

    public Downloader get() {
        mRequest.get();
        return this;
    }

    public Downloader post() {
        post(null);
        return this;
    }

    public Downloader post(Map<String, String> params) {
        FormBody.Builder builder = new FormBody.Builder();
        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (entry.getKey() == null || entry.getKey().equals("")) {
                    continue;
                }
                builder.addEncoded(entry.getKey(), entry.getValue() == null ? "":entry.getValue());
            }
        }
        mRequest.post(builder.build());
        return this;
    }

    public Downloader listener(OnDownloadListener listener) {
        mOnDownloadListener = listener;
        return this;
    }

    public void download() {
        if (mOnDownloadListener == null) throw new NullPointerException("OnDownloadListener must not be empty");

        Call call = HttpClient.getInstance().getOkHttpClient().newCall(mRequest.build());
        mFile = mOnDownloadListener.onStart(call);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mOnDownloadListener.onFailure();
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    if (!response.isSuccessful()) {
                        mOnDownloadListener.onFailure();
                        return;
                    }
                    if (mFile.exists()) mFile.delete();

                    File tmp = new File(mFile.getPath() + ".tmp");
                    if (!tmp.exists()) tmp.createNewFile();
                    InputStream is = response.body().byteStream();
                    FileOutputStream fos = new FileOutputStream(tmp);

                    long now = 0l, length = response.body().contentLength();
                    int i = 0;
                    byte[] buffer = new byte[32*1024];
                    while ((i = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, i);
                        now += i;
                        mOnDownloadListener.onProgress(now, length, (now + 0.0f) / length);
                    }
                    fos.close();
                    is.close();
                    tmp.renameTo(mFile);
                    mOnDownloadListener.onSucceed(mFile);
                } catch (IOException e) {
                    e.printStackTrace();
                    mOnDownloadListener.onFailure();
                }
            }
        });
    }

    public interface OnDownloadListener {

        File onStart(Call call);

        void onProgress(long now, long length, float progress);

        void onSucceed(File file);

        void onFailure();
    }
}
