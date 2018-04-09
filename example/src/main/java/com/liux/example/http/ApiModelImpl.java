package com.liux.example.http;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.liux.http.Http;
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
        Http.get().getService(ApiService.class).queryWeather(
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
        Http.get().getService(ApiService.class).queryIP(
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
        Http.get().getService(ApiService.class).queryMobile(
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
        Http.get().getService(ApiService.class).queryExpress(
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
        Http.get().getService(ApiService.class).testTimeout(
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
        Http.get().getService(ApiService.class).testTimeoutGlobal(
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
    public void testGet(int id, String name) {
        Http.get().getService(ApiService.class).testGet(
                id,
                name
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
    public void testPostBody(int id, String name) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", id);
        jsonObject.put("name", name);
        Http.get().getService(ApiService.class).testPostBody(
                HttpUtil.parseJson(jsonObject.toJSONString())
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
    public void testPostForm(int id, String name) {
        Http.get().getService(ApiService.class).testPostForm(
                id,
                name,
                id,
                name
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
    public void testPostMultipart(int id, String name, File file, byte[] bytes, InputStream stream) {
        Http.get().getService(ApiService.class).testPostMultipart(
                id,
                name,
                id,
                name,
                HttpUtil.parseFilePart("file", file),
                HttpUtil.parseBytePart("bytes", null, bytes),
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
