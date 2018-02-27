package com.liux.example.http;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.liux.example.R;
import com.liux.http.HttpClient;
import com.liux.http.progress.OnProgressListener;
import com.liux.http.progress.OnResponseProgressListener;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;

/**
 * Created by Liux on 2017/11/28.
 */

public class HTTPActivity extends AppCompatActivity {

    @BindView(R.id.et_data)
    EditText etData;
    private ApiModel mApiModle = new ApiModelImpl();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 动态设置全局BaseUrl
        HttpClient.getInstance().setBaseUrl("http://api.ip138.com/");

        // 动态设置全局BaseUrl规则
        HttpClient.getInstance().putDomainRule("138", "http://api.ip138.com/");

        setContentView(R.layout.activity_http);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_weather, R.id.btn_ip, R.id.btn_mobile, R.id.btn_express, R.id.btn_get, R.id.btn_post})
    public void onViewClicked(View view) {
        String data = etData.getText().toString();
        switch (view.getId()) {
            case R.id.btn_weather:
                mApiModle.queryWeather(data);
                break;
            case R.id.btn_ip:
                mApiModle.queryIP(data);
                break;
            case R.id.btn_mobile:
                mApiModle.queryMobile(data);
                break;
            case R.id.btn_express:
                mApiModle.queryExpress(data);
                break;
            case R.id.btn_get:
                HttpClient.getInstance().get(data)
                        .addHeader("AAA", "bbb")
                        .addQuery("name", "Liux")
                        .progress(new OnResponseProgressListener() {
                            @Override
                            public void onResponseProgress(HttpUrl httpUrl, long bytesRead, long contentLength, boolean done) {
                                System.out.println("onResponseProgress:" + httpUrl + "," + bytesRead + "," + contentLength);
                            }
                        })
                        .async(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                System.out.println("onFailure:" + e);
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                System.out.println("onResponse:" + response.body().bytes().length);
                            }
                        });
                break;
            case R.id.btn_post:
                HttpClient.getInstance().post(data)
                        .addHeader("AAA", "bbb")
                        .addQuery("name", "Liux")
                        .addParam("name", "Liux")
                        .addParam("file", new File(getExternalCacheDir() + "/1.apk"))
                        .progress(new OnProgressListener() {
                            @Override
                            public void onResponseProgress(HttpUrl httpUrl, long bytesRead, long contentLength, boolean done) {
                                System.out.println("onResponseProgress:" + httpUrl + "," + bytesRead + "," + contentLength);
                            }

                            @Override
                            public void onRequestProgress(HttpUrl httpUrl, long bytesWrite, long contentLength, boolean done) {
                                System.out.println("onRequestProgress:" + httpUrl + "," + bytesWrite + "," + contentLength);
                            }
                        })
                        .async(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                System.out.println("onFailure:" + e);
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                System.out.println("onResponse:" + response.body().bytes().length);
                            }
                        });
                break;
        }
    }
}
