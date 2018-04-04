package com.liux.example.http;

import com.alibaba.fastjson.JSONObject;
import com.liux.http.HttpClient;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Query;

/**
 * Created by Liux on 2018/1/16.
 */

public interface ApiService {

    @GET("weather/")
    @Headers({
            HttpClient.HEADER_BASE_URL + "http://api.ip138.com/",
            HttpClient.HEADER_TIMEOUT_CONNECT + "3",
            HttpClient.HEADER_TIMEOUT_WRITE + "6",
            HttpClient.HEADER_TIMEOUT_READ + "6"
    })
    Observable<JSONObject> queryWeather(
            @Header("token") String token,
            @Query("code") String code,
            @Query("type") String type
    );

    @GET("query/")
    @Headers({
            HttpClient.HEADER_BASE_RULE + "138"
    })
    Observable<JSONObject> queryIP(
            @Header("token") String token,
            @Query("ip") String ip
    );

    @GET("mobile/")
    @Headers({
            HttpClient.HEADER_TIMEOUT_CONNECT + "7",
            HttpClient.HEADER_TIMEOUT_WRITE + "12",
            HttpClient.HEADER_TIMEOUT_READ + "12"
    })
    Observable<JSONObject> queryMobile(
            @Header("token") String token,
            @Query("mobile") String mobile
    );

    // 以"/"开头的表示从根路径开始
    @GET("/express/info/#123455")
    Observable<JSONObject> queryExpress(
            @Header("token") String token,
            @Query("code") String code,
            @Query("type") String type
    );
}
