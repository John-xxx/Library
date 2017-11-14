package com.liux.http;

import android.content.Context;
import android.util.Log;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.fastjson.FastJsonConverterFactory;

/**
 * 基于 Retrofit2 和 OkHttp3 实现的HttpClient <br>
 * 0.全局单例模式 <br>
 * 1.GET/POST/POST二进制三种模式的同步/异步访问方式 <br>
 * 2.基于 RxJava2 封装 <br>
 * 3.数据解析默认使用 FastJson <br>
 * 4.添加定制请求头和参数的统一接口
 * @author Liux
 */

public class HttpClient {
    private static volatile HttpClient mInstance;
    public static HttpClient getInstance() {
        if (mInstance == null) throw new NullPointerException("HttpClient has not been initialized");
        return mInstance;
    }
    public static void initialize(Context context, String baseUrl) {
        initialize(context, baseUrl, null);
    }
    public static void initialize(Context context, String baseUrl, OkHttpClient.Builder builder) {
        if (mInstance != null) return;
        synchronized(HttpClient.class) {
            if (mInstance != null) return;
            mInstance = new HttpClient(context, baseUrl, builder);
        }
    }

    private static final String TAG = "[HttpClient]";
    private static final MediaType MEDIATYPE_TEXT =  MediaType.parse("application/json; charset=UTF-8");

    private Context mContext;
    private Retrofit mRetrofit;
    private CookieJar mCookieJar;
    private OkHttpClient mOkHttpClient;

    private OnCheckHeadersListener mOnCheckHeadersListener;
    private OnCheckParamsListener mOnCheckParamsListener;

    /* 应用拦截器 */
    private Interceptor mInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            if (request == null) throw new IOException("Request is null");

            Request.Builder requestBuilder = request.newBuilder();

            /* 请求定制：自定义请求头 */
            Map<String, String> headers = new HashMap<>();
            // 移除Accept-Encoding请求头,OkHttp会自动添加
            // 手动添加时OkHttp不会自动解压响应数据
            // headers.put("Accept", "text/html,application/json,application/xhtml+xml,application/xml,image/*");
            // headers.put("Accept-Charset", "utf-8");
            // headers.put("Accept-Language", "zh-CN");
            // headers.put("Accept-Encoding", "gzip,deflate");
            // headers.put("Cache-Control", "no-cache");
            headers.put("User-Agent", HttpUtil.getDefaultUserAgent(mContext));

            if (mOnCheckHeadersListener != null) mOnCheckHeadersListener.onCheckHeaders(request, headers);

            for(Map.Entry<String, String> entry:headers.entrySet()){
                requestBuilder.header(entry.getKey(), entry.getValue());
            }

            /* 请求体定制：自定义参数(针对文本型) */
            RequestBody requestBody = request.body();
            if (mOnCheckParamsListener != null && requestBody != null) {
                if ("GET".equals(request.method().toUpperCase())) {
                    checkGetParams(request, requestBuilder);
                } else if ("POST".equals(request.method().toUpperCase())){
                    if (requestBody.contentLength() == -1) {
                        requestBody = checkPostParams(request);
                    } else if (requestBody instanceof FormBody) {
                        requestBody = checkPostFormParams(request);
                    } else if (requestBody instanceof MultipartBody) {
                        requestBody = checkPostMultipartParams(request);
                    }
                }
            }

            request = requestBuilder.method(request.method(), requestBody).build();

            // 请求前

            Response response = chain.proceed(request);

            // 请求后

            return response;
        }

        /**
         * 逆向解析GET请求并检查参数
         * @param request
         * @return
         */
        private void checkGetParams(Request request, Request.Builder requestBuilder) {
            Map<String, String> params = new IdentityHashMap<>();

            Set<String> set = request.url().queryParameterNames();
            for (String key : set) {
                params.put(new String(key), request.url().queryParameter(key));
            }

            mOnCheckParamsListener.onCheckParams(request, params);

            HttpUrl.Builder builder = request.url().newBuilder();
            for (Map.Entry<String, String> param : params.entrySet()) {
                builder.removeAllQueryParameters(param.getKey());
                builder.addQueryParameter(param.getKey(), param.getValue());
            }
            requestBuilder.url(builder.build());
        }

        /**
         * 逆向解析空参数POST请求并检查参数
         * @param request
         * @return
         */
        private RequestBody checkPostParams(Request request) {
            Map<String, String> params = new IdentityHashMap<>();

            mOnCheckParamsListener.onCheckParams(request, params);

            FormBody.Builder builder = new FormBody.Builder();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.addEncoded(entry.getKey(), entry.getValue());
            }
            return builder.build();
        }

        /**
         * 逆向解析 FormBody 并检查参数
         * @param request
         * @return
         */
        private RequestBody checkPostFormParams(Request request) {
            FormBody oldFormBody = (FormBody) request.body();

            Map<String, String> params = new IdentityHashMap<>();

            if (oldFormBody != null) {
                for (int i = 0; i < oldFormBody.size(); i++) {
                    params.put(new String(oldFormBody.name(i)), oldFormBody.value(i));
                }
            }

            mOnCheckParamsListener.onCheckParams(request, params);

            FormBody.Builder builder = new FormBody.Builder();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.addEncoded(entry.getKey(), entry.getValue());
            }
            return builder.build();
        }

        /**
         * 逆向解析 MultipartBody 并检查参数
         * @param request
         * @return
         */
        private RequestBody checkPostMultipartParams(Request request) {
            MultipartBody oldMultipartBody = (MultipartBody) request.body();

            List<MultipartBody.Part> oldParts = new ArrayList<>();
            Map<String, String> params = new IdentityHashMap<>();

            if (oldMultipartBody != null && oldMultipartBody.parts() != null) {
                List<MultipartBody.Part> parts = oldMultipartBody.parts();
                for (MultipartBody.Part part : parts) {
                    try {
                        Headers head  = part.headers();
                        RequestBody body  = part.body();
                        MediaType type = body.contentType();
                        if (type != null && MEDIATYPE_TEXT.equals(type)) {
                            Buffer buffer = new Buffer();
                            body.writeTo(buffer);
                            String key = head.value(0).substring(head.value(0).lastIndexOf("=") + 1).replace("\"", "");
                            String value = HttpUtil.json2String(buffer.readUtf8());
                            params.put(new String(key), value);
                        } else {
                            oldParts.add(part);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            mOnCheckParamsListener.onCheckParams(request, params);

            MultipartBody.Builder builder = new MultipartBody.Builder();
            // 修复默认为 multipart/mixed 时,PHP 无法识别的问题
            builder.setType(MultipartBody.FORM);
            for (MultipartBody.Part part : oldParts) {
                builder.addPart(part);
            }
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.addFormDataPart(entry.getKey(), entry.getValue());
            }
            return builder.build();
        }
    };

    /* 网络拦截器,根本他吖的不能在这里修改参数!!! */
    private Interceptor mNetworkInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            // 请求前部分

            Response response = chain.proceed(request);
            // 请求后部分

            return response;
        }
    };

    private HttpClient(Context context, String baseUrl, OkHttpClient.Builder builder) {
        if (context == null) throw new NullPointerException("Context required.");
        if (baseUrl == null) throw new NullPointerException("Base URL required.");

        mContext = context.getApplicationContext();

        if (builder != null) {
            mOkHttpClient = builder
                    .addInterceptor(mInterceptor)
                    .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
                    .addNetworkInterceptor(mNetworkInterceptor)
                    .build();
        } else {
            File cacheDir = mContext.getExternalCacheDir();
            if (cacheDir == null || !cacheDir.exists()) cacheDir = mContext.getCacheDir();

            mCookieJar = new PersistentCookieJar(
                    new SetCookieCache(),
                    new SharedPrefsCookiePersistor(mContext)
            );

            mOkHttpClient = new OkHttpClient.Builder()
                    .cookieJar(mCookieJar)
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .cache(new Cache(cacheDir.getAbsoluteFile(), 200 * 1024 * 1024))
                    .retryOnConnectionFailure(true)
                    .addInterceptor(mInterceptor)
                    .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
                    .addNetworkInterceptor(mNetworkInterceptor)
                    .build();
        }

        mRetrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(mOkHttpClient)
                .addConverterFactory(FastJsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    /**
     * 异步GET方式访问HTTP
     * @param url      地址
     * @param callback 回调接口
     */
    public void getAsync(String url, Callback callback) {
        try {
            get(url, callback);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 同步GET方式访问HTTP
     * @param url      地址
     * @return 响应*/
    public Response getSync(String url) throws IOException {
        return get(url, null);
    }

    private Response get(String url, Callback callback) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        if (callback != null){
            Call call = mOkHttpClient.newCall(request);
            call.enqueue(callback);
        } else {
            Call call = mOkHttpClient.newCall(request);
            return call.execute();
        }
        return null;
    }

    /**
     * 异步POST方式访问HTTP
     * @param url  地址
     * @param params  参数支持String
     * @param callback  回调接口
     */
    public void postAsync(String url, Map<String, String> params, Callback callback) {
        try {
            post(url, params, callback);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 同步POST方式访问HTTP
     * @param url  地址
     * @param params  参数支持String
     * @return  响应
     */
    public Response postSync(String url, Map<String, String> params) throws IOException {
        return post(url, params, null);
    }

    private Response post(String url, Map<String, String> params, Callback callback) throws IOException {
        FormBody.Builder builder = new FormBody.Builder();
        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (entry.getKey() == null || entry.getKey().equals("")) {
                    Log.e(TAG, "post: Params key is null");
                    continue;
                }
                builder.add(entry.getKey(), entry.getValue() == null ? "" : entry.getValue());
            }
        }
        RequestBody requestbody = builder.build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestbody)
                .build();

        if (callback != null){
            Call call = mOkHttpClient.newCall(request);
            call.enqueue(callback);
        } else {
            Call call = mOkHttpClient.newCall(request);
            return call.execute();
        }
        return null;
    }

    /**
     * 异步POST方式访问HTTP
     * @param url      地址
     * @param params   参数支持String/File
     * @param callback 回调接口
     */
    public void postMultipartAsync(String url, Map<String, Object> params, Callback callback) {
        try {
            postMultipart(url, params, callback);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 同步POST方式访问HTTP上传二进制流
     * @param url  地址
     * @param params  参数支持String/File
     * @return  响应
     */
    public Response postMultipartSync(String url, Map<String, Object> params) throws IOException {
        return postMultipart(url, params, null);
    }

    private Response postMultipart(String url, Map<String, Object> params, Callback callback) throws IOException {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        // 修复默认为 multipart/mixed 时,PHP 无法识别的问题
        builder.setType(MultipartBody.FORM);
        if (params != null) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                if (entry.getValue() instanceof String) {
                    if (entry.getKey() == null || entry.getKey().equals("")) {
                        Log.e(TAG, "postMultipart: Params key is null");
                        continue;
                    }
                    builder.addFormDataPart(entry.getKey(), entry.getValue() == null ? "" : (String)entry.getValue());
                } else if (entry.getValue() instanceof File) {
                    if (entry.getKey() == null || entry.getKey().equals("")) {
                        Log.e(TAG, "postMultipart: Params key is null");
                        continue;
                    }
                    File file = (File) entry.getValue();
                    if (!file.exists()) {
                        Log.e(TAG, "postMultipart: Value not exists");
                        continue;
                    }
                    builder.addFormDataPart(entry.getKey(), file.getName(), RequestBody.create(HttpUtil.getContentType(file), file));
                } else {
                    if (entry.getKey() == null || entry.getKey().equals("")) {
                        Log.e(TAG, "postMultipart: Params key is null");
                        continue;
                    }
                    builder.addFormDataPart(entry.getKey(), entry.getValue() == null ? "" : String.valueOf(entry.getValue()));
                }
            }
        }

        RequestBody requestbody = builder.build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestbody)
                .build();

        if (callback != null){
            Call call = mOkHttpClient.newCall(request);
            call.enqueue(callback);
        } else {
            Call call = mOkHttpClient.newCall(request);
            return call.execute();
        }
        return null;
    }

    public Retrofit getRetrofit() {
        return mRetrofit;
    }

    public <T> T getRetrofitService(Class<T> service) {
        return getRetrofit().create(service);
    }

    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }

    public void setOnCheckHeadersListener(OnCheckHeadersListener listener) {
        mOnCheckHeadersListener = listener;
    }

    public void setOnCheckParamsListener(OnCheckParamsListener listener) {
        mOnCheckParamsListener = listener;
    }

    public interface OnCheckHeadersListener {
        void onCheckHeaders(Request request, Map<String, String> headers);
    }

    public interface OnCheckParamsListener {
        void onCheckParams(Request request, Map<String, String> params);
    }
}