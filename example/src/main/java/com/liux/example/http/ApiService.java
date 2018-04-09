package com.liux.example.http;

import com.alibaba.fastjson.JSONObject;
import com.liux.http.Http;

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
            Http.HEADER_BASE_URL + ":http://api.ip138.com/"
    })
    Observable<JSONObject> queryWeather(
            @Header("token") String token,
            @Query("code") String code,
            @Query("type") String type
    );

    @GET("query/")
    @Headers({
            Http.HEADER_BASE_RULE + ":138"
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
            Http.HEADER_TIMEOUT_CONNECT + ":3",
            Http.HEADER_TIMEOUT_WRITE + ":6",
            Http.HEADER_TIMEOUT_READ + ":6"
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
            @Query("id") int id,
            @Query("name") String name
    );

    @FormUrlEncoded
    @POST("api/test-post")
    Observable<JSONObject> testPost(
            @Query("id") int id,
            @Query("name") String name,
            @Field("id") int id2,
            @Field("name") String name2
    );

    @Multipart
    @POST("api/test-post-multipart")
    @Headers({
            Http.HEADER_BASE_URL + ":http://192.168.18.15:8080/"
    })
    Observable<JSONObject> testPostMultipart(
            @Query("id") int id,
            @Query("name") String name,
            @Part("id") int id2,
            @Part("name") String name2,
            @Part MultipartBody.Part file,
            @Part MultipartBody.Part aByte,
            @Part MultipartBody.Part stream
    );
}
