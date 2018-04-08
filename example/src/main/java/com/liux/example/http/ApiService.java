package com.liux.example.http;

import com.alibaba.fastjson.JSONObject;
import com.liux.http.HttpClient;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * Created by Liux on 2018/1/16.
 */

public interface ApiService {

    @GET("weather/")
    @Headers({
            HttpClient.HEADER_BASE_URL + ":http://api.ip138.com/"
    })
    Observable<JSONObject> queryWeather(
            @Header("token") String token,
            @Query("code") String code,
            @Query("type") String type
    );

    @GET("query/")
    @Headers({
            HttpClient.HEADER_BASE_RULE + ":138"
    })
    Observable<JSONObject> queryIP(
            @Header("token") String token,
            @Query("ip") String ip
    );

    @GET("mobile/")
    Observable<JSONObject> queryMobile(
            @Header("token") String token,
            @Query("mobile") String mobile
    );

    // 以"/"开头的表示从根路径开始
    @GET("/express/info/")
    Observable<JSONObject> queryExpress(
            @Header("token") String token,
            @Query("no") String no
    );

    // 以"/"开头的表示从根路径开始
    @GET("api/test-timeout")
    @Headers({
            HttpClient.HEADER_TIMEOUT_CONNECT + ":3",
            HttpClient.HEADER_TIMEOUT_WRITE + ":6",
            HttpClient.HEADER_TIMEOUT_READ + ":6"
    })
    Observable<JSONObject> testTimeout(
            @Query("data") String data
    );

    // 以"/"开头的表示从根路径开始
    @GET("api/test-timeout-global")
    Observable<JSONObject> testTimeoutGlobal(
            @Query("data") String data
    );

    @GET("api/test-get")
    Observable<JSONObject> testGet(
            @Query("int") int i,
            @Query("string") String s
    );

    @FormUrlEncoded
    @POST("api/test-post")
    Observable<JSONObject> testPost(
            @Query("int") int i,
            @Query("string") String s,
            @Field("int") int i1,
            @Field("string") String s1
    );

    @Multipart
    @POST("api/test-post-multipart")
    Observable<JSONObject> testPostMultipart(
            @Query("int") int i,
            @Query("string") String s,
            @Part("int") int i1,
            @Part("string") String s1,
            @Part MultipartBody.Part file,
            @Part MultipartBody.Part aByte,
            @Part MultipartBody.Part stream
    );
}
