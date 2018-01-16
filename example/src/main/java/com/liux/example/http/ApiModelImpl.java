package com.liux.example.http;

import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.liux.http.HttpClient;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Liux on 2018/1/16.
 */

public class ApiModelImpl implements ApiModel {

    private static final String TAG = "ApiModelImpl";

    @Override
    public void queryWeather(String code) {
        HttpClient.getInstance().getService(ApiService.class).queryWeather(
                "bd15e11291d68ff100ca0be6ad32b15d",
                "510101",
                "7"
        )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<JSONObject>() {
                    @Override
                    public void onNext(JSONObject jsonObject) {
                        Log.d(TAG, "onNext" + jsonObject.toJSONString());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError", e);
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
                ""
        )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<JSONObject>() {
                    @Override
                    public void onNext(JSONObject jsonObject) {
                        Log.d(TAG, "onNext" + jsonObject.toJSONString());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError", e);
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
                "13086668581"
        )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<JSONObject>() {
                    @Override
                    public void onNext(JSONObject jsonObject) {
                        Log.d(TAG, "onNext" + jsonObject.toJSONString());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError", e);
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
                "bd15e11291d68ff100ca0be6ad32b15d",
                "510101",
                "7"
        )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<JSONObject>() {
                    @Override
                    public void onNext(JSONObject jsonObject) {
                        Log.d(TAG, "onNext" + jsonObject.toJSONString());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError", e);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete");
                    }
                });
    }
}
