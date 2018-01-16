package com.liux.example.http;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.liux.example.R;
import com.liux.http.HttpClient;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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

    @OnClick({R.id.btn_weather, R.id.btn_ip, R.id.btn_mobile, R.id.btn_express})
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
        }
    }
}
