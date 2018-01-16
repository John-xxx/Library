package com.liux.example.http;

/**
 * Created by Liux on 2018/1/16.
 */

public interface ApiModel {

    void queryWeather(String code);

    void queryIP(String ip);

    void queryMobile(String mobile);

    void queryExpress(String code);
}
