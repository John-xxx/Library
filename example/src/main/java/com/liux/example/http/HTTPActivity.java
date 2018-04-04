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
import com.liux.view.SingleToast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

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
    private static final String TAG = "HTTPActivity";

    @BindView(R.id.et_data)
    EditText etData;

    private ApiModel mApiModle = new ApiModelImpl(this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 动态设置全局BaseUrl
        HttpClient.getInstance().setBaseUrl("http://api.ip138.com/v1.0/");

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
                // 设置Base,正常获取数据
                mApiModle.queryWeather(data);
                break;
            case R.id.btn_ip:
                // 设置Base-Rule,正常获取数据
                mApiModle.queryIP(data);
                break;
            case R.id.btn_mobile:
                // 使用全局 Base, 404
                mApiModle.queryMobile(data);
                break;
            case R.id.btn_express:
                // 使用全局Base,但使用根路径,正常获取数据
                mApiModle.queryExpress(data);
                break;
            case R.id.btn_get:
                if (HttpUrl.parse(data) == null) {
                    SingleToast.makeText(this, "URL不正确,必须形如 http://www.domain.com/", SingleToast.LENGTH_LONG).show();
                    etData.setText("http://6xyun.cn/");
                    return;
                }
                HttpClient.getInstance().get(data)
                        .addHeader("AAA", "bbb")
                        .addQuery("name", "Liux")
                        .fragment("what")
                        .connectTimeout(5)
                        .writeTimeout(10)
                        .readTimeout(10)
                        .distinguishRequest(true)
                        .progress(new OnResponseProgressListener() {
                            @Override
                            public void onResponseProgress(final HttpUrl httpUrl, final long bytesRead, final long contentLength, boolean done) {
                                System.out.println("onResponseProgress:" + httpUrl + "," + bytesRead + "," + contentLength);
                                etData.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        SingleToast.makeText(HTTPActivity.this, "onResponseProgress:" + httpUrl + "," + bytesRead + "," + contentLength, SingleToast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        })
                        .async(new Callback() {
                            @Override
                            public void onFailure(Call call, final IOException e) {
                                System.out.println("onFailure:" + e);
                                etData.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        SingleToast.makeText(HTTPActivity.this, "onFailure:" + e, SingleToast.LENGTH_LONG).show();
                                    }
                                });
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                final long length = response.body().bytes().length;
                                System.out.println("onResponse:" + length);
                                etData.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        SingleToast.makeText(HTTPActivity.this, "onResponse:" + length, SingleToast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        });
                break;
            case R.id.btn_post:
                if (HttpUrl.parse(data) == null) {
                    SingleToast.makeText(this, "URL不正确,必须形如 http://www.domain.com/", SingleToast.LENGTH_LONG).show();
                    etData.setText("http://6xyun.cn/");
                    return;
                }
                File temp = null;
                FileOutputStream fileOutputStream = null;
                try {
                    temp = File.createTempFile("temp_", ".txt");
                    fileOutputStream = new FileOutputStream(temp);

                    String str = "012345678vasdjhklsadfqwiurewopt";
                    int random = new Random().nextInt(100) + 100;
                    int len = str.length();
                    for (int i = 0; i < random; i++) {
                        StringBuilder s = new StringBuilder();
                        for (int j = 0; j < random; j++) {
                            s.append(str.charAt((int)(Math.random() * len)));
                        }
                        fileOutputStream.write(s.toString().getBytes());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (temp != null) temp.deleteOnExit();
                    if (fileOutputStream != null) {
                        try {
                            fileOutputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                HttpClient.getInstance().post(data)
                        .addHeader("AAA", "bbb")
                        .addQuery("name", "Liux")
                        .addParam("name", "Liux")
                        .addParam("file", temp)
                        .fragment("what")
                        .connectTimeout(5)
                        .writeTimeout(10)
                        .readTimeout(10)
                        .distinguishRequest(true)
                        .progress(new OnProgressListener() {
                            @Override
                            public void onResponseProgress(final HttpUrl httpUrl, final long bytesRead, final long contentLength, boolean done) {
                                System.out.println("onResponseProgress:" + httpUrl + "," + bytesRead + "," + contentLength);
                                etData.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        SingleToast.makeText(HTTPActivity.this, "onResponseProgress:" + httpUrl + "," + bytesRead + "," + contentLength, SingleToast.LENGTH_LONG).show();
                                    }
                                });
                            }

                            @Override
                            public void onRequestProgress(final HttpUrl httpUrl, final long bytesWrite, final long contentLength, boolean done) {
                                System.out.println("onRequestProgress:" + httpUrl + "," + bytesWrite + "," + contentLength);
                                etData.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        SingleToast.makeText(HTTPActivity.this, "onRequestProgress:" + httpUrl + "," + bytesWrite + "," + contentLength , SingleToast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        })
                        .async(new Callback() {
                            @Override
                            public void onFailure(Call call, final IOException e) {
                                System.out.println("onFailure:" + e);
                                etData.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        SingleToast.makeText(HTTPActivity.this, "onFailure:" + e, SingleToast.LENGTH_LONG).show();
                                    }
                                });
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                final long length = response.body().bytes().length;
                                System.out.println("onResponse:" + length);
                                etData.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        SingleToast.makeText(HTTPActivity.this, "onResponse:" + length, SingleToast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        });
                break;
        }
    }
}
