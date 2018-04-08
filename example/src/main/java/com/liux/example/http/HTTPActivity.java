package com.liux.example.http;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.alibaba.fastjson.JSONObject;
import com.liux.example.R;
import com.liux.http.HttpClient;
import com.liux.http.HttpUtil;
import com.liux.http.progress.OnProgressListener;
import com.liux.http.progress.OnResponseProgressListener;
import com.liux.view.SingleToast;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Arrays;
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

    @OnClick({R.id.btn_retorfit_get, R.id.btn_retorfit_post, R.id.btn_retorfit_post_multipart, R.id.btn_retorfit_base_header, R.id.btn_retorfit_base_header_rule, R.id.btn_retorfit_base_global, R.id.btn_retorfit_base_global_root, R.id.btn_retorfit_timeout_header, R.id.btn_retorfit_timeout_global})
    public void onRetorfitClicked(View view) {
        String data = etData.getText().toString();
        switch (view.getId()) {
            case R.id.btn_retorfit_get:
                mApiModle.testGet(
                        123,
                        "liux"
                );
                break;
            case R.id.btn_retorfit_post:
                mApiModle.testPost(
                        123,
                        "liux"
                );
                break;
            case R.id.btn_retorfit_post_multipart:
                mApiModle.testPostMultipart(
                        123,
                        "liux",
                        getTempFile(),
                        getTempBytes(),
                        getTempInputStream()
                );
                break;
            case R.id.btn_retorfit_base_header:
                // 设置Base,正常获取数据
                mApiModle.queryWeather(data);
                break;
            case R.id.btn_retorfit_base_header_rule:
                // 设置Base-Rule,正常获取数据
                mApiModle.queryIP(data);
                break;
            case R.id.btn_retorfit_base_global:
                // 使用全局 Base, 404
                mApiModle.queryMobile(data);
                break;
            case R.id.btn_retorfit_base_global_root:
                // 使用全局Base,但使用根路径,正常获取数据
                mApiModle.queryExpress(data);
                break;
            case R.id.btn_retorfit_timeout_header:
                mApiModle.testTimeout(data);
                break;
            case R.id.btn_retorfit_timeout_global:
                HttpClient.getInstance().setOverallConnectTimeout(5);
                HttpClient.getInstance().setOverallWriteTimeout(20);
                HttpClient.getInstance().setOverallReadTimeout(20);
                mApiModle.testTimeoutGlobal(data);
                break;
        }
    }

    @OnClick({R.id.btn_request_get, R.id.btn_request_post_body, R.id.btn_request_post_form, R.id.btn_request_post_multipart, R.id.btn_request_timeout_header, R.id.btn_request_timeout_global})
    public void onRequestClicked(View view) {
        String data = etData.getText().toString();
        if (HttpUrl.parse(data) == null) {
            SingleToast.makeText(this, "URL不正确,必须形如 http://www.domain.com/", SingleToast.LENGTH_LONG).show();
            etData.setText("http://6xyun.cn/");
            return;
        }
        switch (view.getId()) {
            case R.id.btn_request_get:
                HttpClient.getInstance().get(data)
                        .addHeader("Request-Header-Id", "btn_request_get")
                        .addQuery("Request-Query-Id", "btn_request_get")
                        .fragment("testFragment")
                        .progress(new OnResponseProgressListener() {
                            @Override
                            public void onResponseProgress(final HttpUrl httpUrl, final long bytesRead, final long contentLength, final boolean done) {
                                System.out.println("onResponseProgress:" + httpUrl + "," + bytesRead + "," + contentLength + "," + done);
                                etData.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        SingleToast.makeText(HTTPActivity.this, "onResponseProgress:" + httpUrl + "," + bytesRead + "," + contentLength + "," + done, SingleToast.LENGTH_LONG).show();
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
            case R.id.btn_request_post_body:
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("Request-Body-Id", "btn_request_post_body");
                HttpClient.getInstance().post(data)
                        .addHeader("Request-Header-Id", "btn_request_post_body")
                        .addQuery("Request-Query-Id", "btn_request_post_body")
                        .body(HttpUtil.parseJson(jsonObject.toJSONString()))
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
            case R.id.btn_request_post_form:
                HttpClient.getInstance().post(data)
                        .addHeader("Request-Header-Id", "btn_request_post_form")
                        .addQuery("Request-Query-Id", "btn_request_post_form")
                        .addParam("Request-Param-Id", "btn_request_post_form")
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
            case R.id.btn_request_post_multipart:
                HttpClient.getInstance().post(data)
                        .addHeader("Request-Header-Id", "btn_request_post_multipart")
                        .addQuery("Request-Query-Id", "btn_request_post_multipart")
                        .addParam("Request-Param-Id", "btn_request_post_multipart")
                        .addParam("file", getTempFile())
                        .addParam("btye", getTempBytes())
                        .addParam("stream", getTempInputStream())
                        .progress(new OnProgressListener() {
                            @Override
                            public void onResponseProgress(final HttpUrl httpUrl, final long bytesRead, final long contentLength, final boolean done) {
                                System.out.println("onResponseProgress:" + httpUrl + "," + bytesRead + "," + contentLength + "," + done);
                                etData.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        SingleToast.makeText(HTTPActivity.this, "onResponseProgress:" + httpUrl + "," + bytesRead + "," + contentLength + "," + done, SingleToast.LENGTH_LONG).show();
                                    }
                                });
                            }

                            @Override
                            public void onRequestProgress(final HttpUrl httpUrl, final long bytesWrite, final long contentLength, final boolean done) {
                                System.out.println("onRequestProgress:" + httpUrl + "," + bytesWrite + "," + contentLength + "," + done);
                                etData.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        SingleToast.makeText(HTTPActivity.this, "onRequestProgress:" + httpUrl + "," + bytesWrite + "," + contentLength + "," + done, SingleToast.LENGTH_LONG).show();
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
            case R.id.btn_request_timeout_header:
                HttpClient.getInstance().post(data)
                        .addHeader("Request-Header-Id", "btn_request_timeout_header")
                        .addQuery("Request-Query-Id", "btn_request_timeout_header")
                        .addParam("Request-Param-Id", "btn_request_timeout_header")
                        .connectTimeout(5)
                        .writeTimeout(10)
                        .readTimeout(10)
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
            case R.id.btn_request_timeout_global:
                HttpClient.getInstance().setOverallConnectTimeout(5);
                HttpClient.getInstance().setOverallWriteTimeout(20);
                HttpClient.getInstance().setOverallReadTimeout(20);
                HttpClient.getInstance().post(data)
                        .addHeader("Request-Header-Id", "btn_request_timeout_header")
                        .addQuery("Request-Query-Id", "btn_request_timeout_header")
                        .addParam("Request-Param-Id", "btn_request_timeout_header")
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

    private File getTempFile() {
        File temp = null;
        FileOutputStream fileOutputStream = null;
        try {
            temp = File.createTempFile("temp_", ".apk");
            fileOutputStream = new FileOutputStream(temp);

            int random = new Random().nextInt(40960) + 102400;

            byte[] bytes = new byte[random];
            for (int i = 0; i < random; i++) {
                bytes[i] = (byte) System.nanoTime();
            }

            MessageDigest digest = MessageDigest.getInstance("SHA1");
            digest.update(bytes);
            System.out.println("getTempFile():" + new BigInteger(1, digest.digest()).toString(32));

            fileOutputStream.write(bytes);
        } catch (Exception e) {
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
        return temp;
    }

    private byte[] getTempBytes() {
        int random = new Random().nextInt(30) + 50;

        byte[] bytes = new byte[random];
        for (int i = 0; i < random; i++) {
            bytes[i] = (byte) System.nanoTime();
        }

        System.out.println("getTempBytes():" + Arrays.toString(bytes));
        return bytes;
    }

    private InputStream getTempInputStream() {
        int random = new Random().nextInt(50) + 200;

        byte[] bytes = new byte[random];
        for (int i = 0; i < random; i++) {
            bytes[i] = (byte) System.nanoTime();
        }

        System.out.println("getTempInputStream():" + Arrays.toString(bytes));
        return new ByteArrayInputStream(bytes);
    }
}
