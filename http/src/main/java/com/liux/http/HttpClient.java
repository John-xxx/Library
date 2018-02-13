package com.liux.http;

import android.content.Context;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.liux.http.converter.FastJsonConverterFactory;
import com.liux.http.interceptor.CheckInterceptor;
import com.liux.http.interceptor.HttpLoggingInterceptor;
import com.liux.http.interceptor.BaseUrlInterceptor;
import com.liux.http.interceptor.UserAgentInterceptor;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;

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

    public static boolean isInitialize() {
        synchronized(HttpClient.class) {
            return mInstance != null;
        }
    }
    public static void initialize(Context context, String baseUrl) {
        initialize(
                context,
                null,
                new Retrofit.Builder().baseUrl(baseUrl)
        );
    }
    public static void initialize(Context context, OkHttpClient.Builder okHttpBuilder, Retrofit.Builder retrofitBuilder) {
        if (mInstance != null) return;
        synchronized(HttpClient.class) {
            if (mInstance != null) return;
            mInstance = new HttpClient(context, okHttpBuilder, retrofitBuilder);
        }
    }

    public static final String TAG = "[HttpClient]";

    private Context mContext;
    private Retrofit mRetrofit;
    private OkHttpClient mOkHttpClient;
    private BaseUrlInterceptor mBaseUrlInterceptor;
    private UserAgentInterceptor mUserAgentInterceptor;
    private CheckInterceptor mCheckInterceptor = new CheckInterceptor();
    private HttpLoggingInterceptor mHttpLoggingInterceptor = new HttpLoggingInterceptor();

    private HttpClient(Context context, OkHttpClient.Builder okHttpBuilder, Retrofit.Builder retrofitBuilder) {
        if (context == null) throw new NullPointerException("Context required.");

        mContext = context.getApplicationContext();

        mBaseUrlInterceptor = new BaseUrlInterceptor(this);
        mUserAgentInterceptor = new UserAgentInterceptor(mContext);

        mOkHttpClient = initOkHttpClient(okHttpBuilder);

        mRetrofit = initRetorfit(retrofitBuilder);
    }

    /**
     * 初始化 OkHttpClient
     * @param okHttpBuilder
     * @return
     */
    private OkHttpClient initOkHttpClient(OkHttpClient.Builder okHttpBuilder) {
        if (okHttpBuilder == null) {
            File cacheDir = mContext.getExternalCacheDir();
            if (cacheDir == null || !cacheDir.exists()) cacheDir = mContext.getCacheDir();

            return new OkHttpClient.Builder()
                    .cookieJar(new PersistentCookieJar(
                            new SetCookieCache(),
                            new SharedPrefsCookiePersistor(mContext)
                    ))
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .cache(new Cache(cacheDir.getAbsoluteFile(), 200 * 1024 * 1024))
                    .retryOnConnectionFailure(true)
                    .addInterceptor(mBaseUrlInterceptor)
                    .addInterceptor(mUserAgentInterceptor)
                    .addInterceptor(mCheckInterceptor)
                    .addInterceptor(mHttpLoggingInterceptor)
                    // 不能通过网络拦截器修改参数
                    // .addNetworkInterceptor()
                    .build();
        } else {
            return okHttpBuilder
                    .addInterceptor(mBaseUrlInterceptor)
                    .addInterceptor(mUserAgentInterceptor)
                    .addInterceptor(mCheckInterceptor)
                    .addInterceptor(mHttpLoggingInterceptor)
                    .build();
        }
    }

    /**
     * 初始化 Retorfit
     * @param retrofitBuilder
     * @return
     */
    private Retrofit initRetorfit(Retrofit.Builder retrofitBuilder) {
        retrofitBuilder
                .client(mOkHttpClient)
                .addConverterFactory(FastJsonConverterFactory.create());

        CallAdapter.Factory factory;
        factory = HttpUtil.getRxJavaCallAdapterFactory();
        if (factory != null) {
            retrofitBuilder.addCallAdapterFactory(factory);
        }
        factory = HttpUtil.getRxJava2CallAdapterFactory();
        if (factory != null) {
            retrofitBuilder.addCallAdapterFactory(factory);
        }

        return retrofitBuilder.build();
    }

    /**
     * 同步 Form 表单请求
     * @param method
     * @param url
     * @param param
     * @return
     * @throws IOException
     */
    public Response syncForm(String method, String url, Map<String, String> param) throws IOException {
        FormBody.Builder builder = new FormBody.Builder();
        if (param != null) {
            for (Map.Entry<String, String> entry : param.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (key != null && value != null) {
                    builder.addEncoded(key, value);
                }
            }
        }
        return sync(method, url, builder.build());
    }

    /**
     * 同步 Multipart 表单请求
     * @param method
     * @param url
     * @param param
     * @return
     * @throws IOException
     */
    public Response syncMultipart(String method, String url, Map<String, Object> param) throws IOException {
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if (param != null) {
            for (Map.Entry<String, Object> entry : param.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (key != null && value != null) {
                    if (value instanceof File) {
                        builder.addPart(HttpUtil.parsePart(key, (File) value));
                    } else {
                        builder.addFormDataPart(key, String.valueOf(value));
                    }
                }
            }
        }
        return sync(method, url, builder.build());
    }

    /**
     * 同步请求
     * @param method
     * @param url
     * @param body
     * @return
     * @throws IOException
     */
    public Response sync(String method, String url, RequestBody body) throws IOException {
        return call(method, url, body, null);
    }

    /**
     * 异步 From 表单请求
     * @param method
     * @param url
     * @param param
     * @param callback
     */
    public void asyncForm(String method, String url, Map<String, String> param, Callback callback) {
        FormBody.Builder builder = new FormBody.Builder();
        if (param != null) {
            for (Map.Entry<String, String> entry : param.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (key != null && value != null) {
                    builder.addEncoded(key, value);
                }
            }
        }
        async(method, url, builder.build(), callback);
    }

    /**
     * 异步 Multipart 表单请求
     * @param method
     * @param url
     * @param param
     * @param callback
     */
    public void asyncMultipart(String method, String url, Map<String, Object> param, Callback callback) {
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if (param != null) {
            for (Map.Entry<String, Object> entry : param.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (key != null && value != null) {
                    if (value instanceof File) {
                        builder.addPart(HttpUtil.parsePart(key, (File) value));
                    } else {
                        builder.addFormDataPart(key, String.valueOf(value));
                    }
                }
            }
        }
        async(method, url, builder.build(), callback);
    }

    /**
     * 异步请求
     * @param method
     * @param url
     * @param body
     * @param callback
     */
    public void async(String method, String url, RequestBody body, Callback callback) {
        try {
            call(method, url, body, callback);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 通用请求
     * @param method
     * @param url
     * @param body
     * @param callback
     * @return
     */
    private Response call(String method, String url, RequestBody body, Callback callback) throws IOException {
        if (!HttpUtil.isHttpMethod(method)) throw new IllegalArgumentException("method is not right");

        Request request = new Request.Builder()
                .method(method, body)
                .url(url)
                .build();

        if (callback != null){
            Call call = mOkHttpClient.newCall(request);
            call.enqueue(callback);
            return null;
        } else {
            Call call = mOkHttpClient.newCall(request);
            return call.execute();
        }
    }

    /**
     * 取 OkHttpClient 实例
     * @return
     */
    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }

    /**
     * 取 Retrofit 实例
     * @return
     */
    public Retrofit getRetrofit() {
        return mRetrofit;
    }

    /**
     * 取 Retrofit 服务
     * @param service
     * @param <T>
     * @return
     */
    public <T> T getService(Class<T> service) {
        return getRetrofit().create(service);
    }

    /**
     * 取当前用户识别标志
     * @return
     */
    public String getUserAgent() {
        return mUserAgentInterceptor.getUserAgent();
    }

    /**
     * 设置当前用户识别标志
     * @param userAgent
     * @return
     */
    public HttpClient setUserAgent(String userAgent) {
        mUserAgentInterceptor.setUserAgent(userAgent);
        return this;
    }

    /**
     * 设置打印日志级别
     * @param level
     * @return
     */
    public HttpClient setLoggingLevel(HttpLoggingInterceptor.Level level) {
        mHttpLoggingInterceptor.setLevel(level);
        return this;
    }

    /**
     * 设置请求头监听
     * @param listener
     * @return
     */
    public HttpClient setOnHeaderListener(OnHeaderListener listener) {
        mCheckInterceptor.setOnHeaderListener(listener);
        return this;
    }

    /**
     * 设置请求监听
     * @param listener
     * @return
     */
    public HttpClient setOnRequestListener(OnRequestListener listener) {
        mCheckInterceptor.setOnRequestListener(listener);
        return this;
    }

    public static final String BASE_URL = BaseUrlInterceptor.BASE_URL + ':';
    public static final String BASE_URL_RULE = BaseUrlInterceptor.BASE_URL_RULE + ':';

    /**
     * 获取当前全局BaseUrl
     * @return
     */
    public String getBaseUrl() {
        return mBaseUrlInterceptor.getBaseUrl();
    }

    /**
     * 设置当前全局BaseUrl
     *
     * @Headers({
     *         HttpClient.BASE_URL + "https://api.domain.com:88/api/"
     * })
     *
     * @param baseUrl
     * @return
     */
    public HttpClient setBaseUrl(String baseUrl) {
        HttpUtil.checkBaseUrl(baseUrl);
        mBaseUrlInterceptor.setBaseUrl(baseUrl);
        return this;
    }

    /**
     * 获取某个规则对应的URL
     * @param rule
     * @return
     */
    public String getDomainRule(String rule) {
        String url = mBaseUrlInterceptor.getDomainRule(rule);
        return url;
    }

    /**
     * 加入某个URL对应的规则
     *
     * @Headers({
     *         HttpClient.BASE_URL_RULE + "{rule}"
     * })
     *
     * @param rule
     * @param baseUrl
     * @return
     */
    public HttpClient putDomainRule(String rule, String baseUrl) {
        HttpUtil.checkBaseUrl(baseUrl);
        mBaseUrlInterceptor.putDomainRule(rule, baseUrl);
        return this;
    }

    /**
     * 获取所有URL对应规则,Copy目的是防止跳过检查添加规则
     * @return
     */
    public Map<String, String> getDomainRules() {
        return mBaseUrlInterceptor.getDomainRules();
    }

    /**
     * 清除所有URL对应规则
     * @return
     */
    public HttpClient clearDomainRules() {
        mBaseUrlInterceptor.clearDomainRules();
        return this;
    }
}