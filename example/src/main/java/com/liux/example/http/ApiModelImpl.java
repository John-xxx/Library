package com.liux.example.http;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.liux.http.HttpClient;
import com.liux.view.SingleToast;

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
}
