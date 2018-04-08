package com.liux.example.http;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.liux.http.HttpClient;
import com.liux.http.HttpUtil;
import com.liux.view.SingleToast;

import java.io.File;
import java.io.InputStream;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Liux on 2018/1/16.
 */

public class ApiModelImpl implements ApiModel {

    private static final String TAG = "ApiModelImpl";

    private Context mContext;

    public ApiModelImpl(Context context) {
        mContext = context;
    }

    @Override
    public void queryWeather(String code) {
        HttpClient.getInstance().getService(ApiService.class).queryWeather(
                "bd15e11291d68ff100ca0be6ad32b15d",
                code,
                "7"
        )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<JSONObject>() {
                    @Override
                    public void onNext(JSONObject jsonObject) {
                        Log.d(TAG, "onNext" + jsonObject.toJSONString());
                        SingleToast.makeText(mContext, "onNext" + jsonObject.toJSONString(), SingleToast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError", e);
                        SingleToast.makeText(mContext, "onError" + e, SingleToast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete");
                    }
                });
    }

    @Override
    public void queryIP(String ip) {
        HttpClient.getInstance().getService(ApiService.class).queryIP(
                "ac1bf65a556b39d1973a40688dacd39f",
                ip
        )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<JSONObject>() {
                    @Override
                    public void onNext(JSONObject jsonObject) {
                        Log.d(TAG, "onNext" + jsonObject.toJSONString());
                        SingleToast.makeText(mContext, "onNext" + jsonObject.toJSONString(), SingleToast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError", e);
                        SingleToast.makeText(mContext, "onError" + e, SingleToast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete");
                    }
                });
    }

    @Override
    public void queryMobile(String mobile) {
        HttpClient.getInstance().getService(ApiService.class).queryMobile(
                "f38fde6a4395eaebe9fd525a96145925",
                mobile
        )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<JSONObject>() {
                    @Override
                    public void onNext(JSONObject jsonObject) {
                        Log.d(TAG, "onNext" + jsonObject.toJSONString());
                        SingleToast.makeText(mContext, "onNext" + jsonObject.toJSONString(), SingleToast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError", e);
                        SingleToast.makeText(mContext, "onError" + e, SingleToast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete");
                    }
                });
    }

    @Override
    public void queryExpress(String code) {
        HttpClient.getInstance().getService(ApiService.class).queryExpress(
                "f13239b8e9de8d5dc90694337d670c39",
                code
        )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<JSONObject>() {
                    @Override
                    public void onNext(JSONObject jsonObject) {
                        Log.d(TAG, "onNext" + jsonObject.toJSONString());
                        SingleToast.makeText(mContext, "onNext" + jsonObject.toJSONString(), SingleToast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError", e);
                        SingleToast.makeText(mContext, "onError" + e, SingleToast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete");
                    }
                });
    }

    @Override
    public void testTimeout(String data) {
        HttpClient.getInstance().getService(ApiService.class).testTimeout(
                data
        )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<JSONObject>() {
                    @Override
                    public void onNext(JSONObject jsonObject) {
                        Log.d(TAG, "onNext" + jsonObject.toJSONString());
                        SingleToast.makeText(mContext, "onNext" + jsonObject.toJSONString(), SingleToast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError", e);
                        SingleToast.makeText(mContext, "onError" + e, SingleToast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete");
                    }
                });
    }

    @Override
    public void testTimeoutGlobal(String data) {
        HttpClient.getInstance().getService(ApiService.class).testTimeoutGlobal(
                data
        )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<JSONObject>() {
                    @Override
                    public void onNext(JSONObject jsonObject) {
                        Log.d(TAG, "onNext" + jsonObject.toJSONString());
                        SingleToast.makeText(mContext, "onNext" + jsonObject.toJSONString(), SingleToast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError", e);
                        SingleToast.makeText(mContext, "onError" + e, SingleToast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete");
                    }
                });
    }

    @Override
    public void testGet(int i, String s) {
        HttpClient.getInstance().getService(ApiService.class).testGet(
                i,
                s
        )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<JSONObject>() {
                    @Override
                    public void onNext(JSONObject jsonObject) {
                        Log.d(TAG, "onNext" + jsonObject.toJSONString());
                        SingleToast.makeText(mContext, "onNext" + jsonObject.toJSONString(), SingleToast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError", e);
                        SingleToast.makeText(mContext, "onError" + e, SingleToast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete");
                    }
                });
    }

    @Override
    public void testPost(int i, String s) {
        HttpClient.getInstance().getService(ApiService.class).testPost(
                i,
                s,
                i,
                s
        )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<JSONObject>() {
                    @Override
                    public void onNext(JSONObject jsonObject) {
                        Log.d(TAG, "onNext" + jsonObject.toJSONString());
                        SingleToast.makeText(mContext, "onNext" + jsonObject.toJSONString(), SingleToast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError", e);
                        SingleToast.makeText(mContext, "onError" + e, SingleToast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete");
                    }
                });
    }

    @Override
    public void testPostMultipart(int i, String s, File file, byte[] bytes, InputStream stream) {
        HttpClient.getInstance().getService(ApiService.class).testPostMultipart(
                i,
                s,
                i,
                s,
                HttpUtil.parseFilePart("file", file),
                HttpUtil.parseBytePart("byte", null, bytes),
                HttpUtil.parseInputStreamPart("stream", null, stream)
        )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<JSONObject>() {
                    @Override
                    public void onNext(JSONObject jsonObject) {
                        Log.d(TAG, "onNext" + jsonObject.toJSONString());
                        SingleToast.makeText(mContext, "onNext" + jsonObject.toJSONString(), SingleToast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError", e);
                        SingleToast.makeText(mContext, "onError" + e, SingleToast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete");
                    }
                });
    }
}
