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

    @GET("weather/#123455")
    @Headers({
            HttpClient.BASE_URL + "http://api.ip138.com/"
    })
    public Observable<JSONObject> queryWeather(
            @Header("token") String token,
            @Query("code") String code,
            @Query("type") String type
    );

    @GET("query/")
    @Headers({
            HttpClient.BASE_URL_RULE + "138"
    })
    public Observable<JSONObject> queryIP(
            @Header("token") String token,
            @Query("ip") String ip
    );

    @GET("mobile/")
    public Observable<JSONObject> queryMobile(
            @Header("token") String token,
            @Query("mobile") String mobile
    );

    @GET("express/info/")
    public Observable<JSONObject> queryExpress(
            @Header("token") String token,
            @Query("code") String code,
            @Query("type") String type
    );
}
