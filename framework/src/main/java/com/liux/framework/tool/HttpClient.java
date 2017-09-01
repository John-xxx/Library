package com.liux.framework.tool;

import android.content.Context;
import android.util.Log;

import com.liux.framework.util.DeviceUtil;
import com.liux.framework.util.TextUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
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

import static okhttp3.MultipartBody.FORM;

/**
 * 采用懒加载单例模式,全局使用一个HttpClient <br>
 * <br>
 * 2016-11-18 <br>
 * 实现了GET/POST/POST二进制三种模式的同步/异步访问方式 <br>
 * <br>
 * 2016-12-5 <br>
 * 基于Retrofit和OkHttpClient重新封装 <br>
 * <br>
 * 2017-8-7 <br>
 * 基于RxJava2封装 <br>
 * 2017-8-14 <br>
 * 优化初始化逻辑
 * 2017-9-1 <br>
 * 已知问题,当开启参数检查时候key重复,value会被覆盖(考虑用List代替Map实现)
 * @author Liux
 */

public class HttpClient {
    private static volatile HttpClient mInstance;
    public static HttpClient getInstance() {
        if (mInstance == null) throw new NullPointerException("HttpClient has not been initialized");
        return mInstance;
    }
    public static void initialize(Context context, String baseUrl) {
        if (mInstance != null) return;
        synchronized(HttpClient.class) {
            if (mInstance != null) return;
            mInstance = new HttpClient(context, baseUrl);
        }
    }

    private static String TAG = "[HttpClient]";

    private Context mContext;
    private Retrofit mRetrofit;
    private OkHttpClient mOkHttpClient;

    private OnCheckHeadersListener mOnCheckHeadersListener;
    private OnCheckParamsListener mOnCheckParamsListener;

    /* 简陋的Cookie管理器 */
    private CookieJar mCookieJar = new CookieJar() {
        private Map<String, List<Cookie>> mCookies = new HashMap<>();

        @Override
        public void saveFromResponse(HttpUrl httpUrl, List<Cookie> list) {
            mCookies.put(httpUrl.host(), list);
        }

        @Override
        public List<Cookie> loadForRequest(HttpUrl httpUrl) {
            List<Cookie> cookie = mCookies.get(httpUrl.host());
            return cookie != null ? cookie : new ArrayList<Cookie>();
        }
    };

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
            headers.put("User-Agent", DeviceUtil.getOSName() + "_" + DeviceUtil.getOSVersion() + "_" + DeviceUtil.getPackageName(mContext) + "_" + DeviceUtil.getVersionName(mContext));

            if (mOnCheckHeadersListener != null) mOnCheckHeadersListener.onCheckHeaders(request, headers);

            for(Map.Entry<String, String> entry:headers.entrySet()){
                requestBuilder.header(entry.getKey(), entry.getValue());
            }

            /* 请求体定制：自定义参数(针对文本型) */
            RequestBody requestBody = request.body();
            if (mOnCheckParamsListener != null && requestBody != null) {
                if (request.method().toUpperCase().equals("GET") ) {
                    Map<String, String> params = new HashMap<>();
                    Set<String> set = request.url().queryParameterNames();

                    for (String name : set) {
                        params.put(name, request.url().queryParameter(name));
                    }

                    mOnCheckParamsListener.onCheckParams(request, params);

                    HttpUrl.Builder builder = request.url().newBuilder();
                    for (Map.Entry<String, String> param : params.entrySet()) {
                        builder.removeAllQueryParameters(param.getKey());
                        builder.addQueryParameter(param.getKey(), param.getValue());
                    }
                    requestBuilder.url(builder.build());
                } else if (request.method().toUpperCase().equals("POST")){
                    if (requestBody.contentLength() == -1) {
                        Map<String, String> params = new HashMap<>();

                        mOnCheckParamsListener.onCheckParams(request, params);

                        FormBody.Builder bodyBuilder = new FormBody.Builder();
                        for (Map.Entry<String, String> entry : params.entrySet()) {
                            bodyBuilder.addEncoded(entry.getKey(), entry.getValue());
                        }
                        requestBody = bodyBuilder.build();
                    } else if (requestBody instanceof FormBody) {
                        Map<String, String> params = new HashMap<>();
                        FormBody oldFormBody = (FormBody) request.body();

                        if (oldFormBody != null) {
                            for (int i = 0; i < oldFormBody.size(); i++) {
                                params.put(oldFormBody.name(i), oldFormBody.value(i));
                            }
                        }

                        mOnCheckParamsListener.onCheckParams(request, params);

                        FormBody.Builder bodyBuilder = new FormBody.Builder();
                        for (Map.Entry<String, String> entry : params.entrySet()) {
                            bodyBuilder.addEncoded(entry.getKey(), entry.getValue());
                        }
                        requestBody = bodyBuilder.build();
                    } else if (requestBody instanceof MultipartBody) {
                        Map<String, String> params = new HashMap<>();
                        List<MultipartBody.Part> oldParts = new ArrayList<>();
                        MultipartBody oldMultipartBody = (MultipartBody) request.body();

                        if (oldMultipartBody != null) {
                            List<MultipartBody.Part> parts = oldMultipartBody.parts();
                            if (parts != null) {
                                for (MultipartBody.Part part : parts) {
                                    try {
                                        /* Java反射出属性(适用于OkHttp3.5及之前版本) */
                                        // Field h = part.getClass().getDeclaredField("headers");
                                        // Field b = part.getClass().getDeclaredField("body");
                                        // h.setAccessible(true);
                                        // b.setAccessible(true);
                                        // /* 改变修饰符 */
                                        // //Field modifiersField = Field.class.getDeclaredFields()[0];
                                        // //modifiersField.setAccessible(true);
                                        // //modifiersField.setInt(h, Modifier.PUBLIC);
                                        // //modifiersField.setInt(b, Modifier.PUBLIC);
                                        // /* 取出值 */
                                        // Headers head  = (Headers)h.get(part);
                                        // RequestBody body  = (RequestBody)b.get(part);
                                        Headers head  = part.headers();
                                        RequestBody body  = part.body();
                                        MediaType type = body.contentType();
                                        if (type != null && type.equals(MediaType.parse("application/json; charset=UTF-8"))) {
                                            Buffer buffer = new Buffer();
                                            body.writeTo(buffer);
                                            String key = head.value(0).substring(head.value(0).lastIndexOf("=") + 1).replace("\"", "");
                                            String value = TextUtil.json2String(buffer.readUtf8());
                                            params.put(key, value);
                                        } else {
                                            oldParts.add(part);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        mOnCheckParamsListener.onCheckParams(request, params);

                        MultipartBody.Builder bodyBuilder = new MultipartBody.Builder();
                        // 修复默认为 multipart/mixed 时,PHP 无法识别的问题
                        bodyBuilder.setType(MultipartBody.FORM);
                        for (Map.Entry<String, String> entry : params.entrySet()) {
                            bodyBuilder.addFormDataPart(entry.getKey(), entry.getValue());
                        }
                        for (MultipartBody.Part part : oldParts) {
                            bodyBuilder.addPart(part);
                        }
                        requestBody = bodyBuilder.build();
                    }
                }
            }

            request = requestBuilder.method(request.method(), requestBody).build();
            // 请求前部分

            Response response = chain.proceed(request);
            // 请求后部分

            return response;
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

    private HttpClient() {

    }

    private HttpClient(Context context, String baseUrl) {
        if (context == null) throw new NullPointerException("Context required.");
        if (baseUrl == null) throw new NullPointerException("Base URL required.");

        mContext = context;

        File cacheDir = mContext.getExternalCacheDir();
        if (cacheDir == null || !cacheDir.exists()) cacheDir = mContext.getCacheDir();

        mOkHttpClient = new OkHttpClient.Builder()
                .cookieJar(mCookieJar)
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .cache(new Cache(cacheDir.getAbsoluteFile(), 100 * 1024 * 1024))
                .retryOnConnectionFailure(true)
                .addInterceptor(mInterceptor)
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
                .addNetworkInterceptor(mNetworkInterceptor)
                .build();

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
                builder.add(entry.getKey(), entry.getValue() == null ? "":entry.getValue());
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
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(FORM);
        if (params != null) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                if (entry.getValue() instanceof String) {
                    if (entry.getKey() == null || entry.getKey().equals("")) {
                        Log.e(TAG, "postMultipart: Params key is null");
                        continue;
                    }
                    builder.addFormDataPart(entry.getKey(), entry.getValue() == null ? "":(String)entry.getValue());
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
                    builder.addFormDataPart(entry.getKey(), file.getName(), RequestBody.create(getContentType(file), file));
                } else {
                    if (entry.getKey() == null || entry.getKey().equals("")) {
                        Log.e(TAG, "postMultipart: Params key is null");
                        continue;
                    }
                    builder.addFormDataPart(entry.getKey(), entry.getValue() == null ? "":String.valueOf(entry.getValue()));
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

    /**
     * 生成一个 {@link MultipartBody.Part}
     * @param param
     * @param file
     * @return
     */
    public static MultipartBody.Part parsePart(String param, File file) {
        RequestBody body = RequestBody.create(getContentType(file), file);
        return MultipartBody.Part.createFormData(param, file.getName(), body);
    }

    private final static MediaType getContentType(File file) {
        String[] s = file.getName().split("\\.");

        String type = "*/*";
        if (s.length > 1) {
            String t = mContentType.get(s[s.length - 1]);
            if (t != null) type = t;
        }

        return MediaType.parse(type);
    }
    private final static Map<String, String> mContentType = new HashMap<>();
    static {
        mContentType.put("hqx","application/mac-binhex40");
        mContentType.put("cpt","application/mac-compactpro");
        mContentType.put("doc","application/msword");
        mContentType.put("bin","application/octet-stream");
        mContentType.put("dms","application/octet-stream");
        mContentType.put("lha","application/octet-stream");
        mContentType.put("lzh","application/octet-stream");
        mContentType.put("exe","application/octet-stream");
        mContentType.put("class","application/octet-stream");
        mContentType.put("so","application/octet-stream");
        mContentType.put("dll","application/octet-stream");
        mContentType.put("oda","application/oda");
        mContentType.put("pdf","application/pdf");
        mContentType.put("ai","application/postscript");
        mContentType.put("eps","application/postscript");
        mContentType.put("ps","application/postscript");
        mContentType.put("smi","application/smil");
        mContentType.put("smil","application/smil");
        mContentType.put("mif","application/vnd.mif");
        mContentType.put("xls","application/vnd.ms-excel");
        mContentType.put("ppt","application/vnd.ms-powerpoint");
        mContentType.put("wbxml","application/vnd.wap.wbxml");
        mContentType.put("wmlc","application/vnd.wap.wmlc");
        mContentType.put("wmlsc","application/vnd.wap.wmlscriptc");
        mContentType.put("bcpio","application/x-bcpio");
        mContentType.put("vcd","application/x-cdlink");
        mContentType.put("pgn","application/x-chess-pgn");
        mContentType.put("cpio","application/x-cpio");
        mContentType.put("csh","application/x-csh");
        mContentType.put("dcr","application/x-director");
        mContentType.put("dir","application/x-director");
        mContentType.put("dxr","application/x-director");
        mContentType.put("dvi","application/x-dvi");
        mContentType.put("spl","application/x-futuresplash");
        mContentType.put("gtar","application/x-gtar");
        mContentType.put("hdf","application/x-hdf");
        mContentType.put("js","application/x-javascript");
        mContentType.put("skp","application/x-koan");
        mContentType.put("skd","application/x-koan");
        mContentType.put("skt","application/x-koan");
        mContentType.put("skm","application/x-koan");
        mContentType.put("latex","application/x-latex");
        mContentType.put("nc","application/x-netcdf");
        mContentType.put("cdf","application/x-netcdf");
        mContentType.put("sh","application/x-sh");
        mContentType.put("shar","application/x-shar");
        mContentType.put("swf","application/x-shockwave-flash");
        mContentType.put("sit","application/x-stuffit");
        mContentType.put("sv4cpio","application/x-sv4cpio");
        mContentType.put("sv4crc","application/x-sv4crc");
        mContentType.put("tar","application/x-tar");
        mContentType.put("tcl","application/x-tcl");
        mContentType.put("tex","application/x-tex");
        mContentType.put("texinfo","application/x-texinfo");
        mContentType.put("texi","application/x-texinfo");
        mContentType.put("t","application/x-troff");
        mContentType.put("tr","application/x-troff");
        mContentType.put("roff","application/x-troff");
        mContentType.put("man","application/x-troff-man");
        mContentType.put("me","application/x-troff-me");
        mContentType.put("ms","application/x-troff-ms");
        mContentType.put("ustar","application/x-ustar");
        mContentType.put("src","application/x-wais-source");
        mContentType.put("xhtml","application/xhtml+xml");
        mContentType.put("xht","application/xhtml+xml");
        mContentType.put("zip","application/zip");
        mContentType.put("au","audio/basic");
        mContentType.put("snd","audio/basic");
        mContentType.put("mid","audio/midi");
        mContentType.put("midi","audio/midi");
        mContentType.put("kar","audio/midi");
        mContentType.put("mpga","audio/mpeg");
        mContentType.put("mp2","audio/mpeg");
        mContentType.put("mp3","audio/mpeg");
        mContentType.put("aif","audio/x-aiff");
        mContentType.put("aiff","audio/x-aiff");
        mContentType.put("aifc","audio/x-aiff");
        mContentType.put("m3u","audio/x-mpegurl");
        mContentType.put("ram","audio/x-pn-realaudio");
        mContentType.put("rm","audio/x-pn-realaudio");
        mContentType.put("rpm","audio/x-pn-realaudio-plugin");
        mContentType.put("ra","audio/x-realaudio");
        mContentType.put("wav","audio/x-wav");
        mContentType.put("pdb","chemical/x-pdb");
        mContentType.put("xyz","chemical/x-xyz");
        mContentType.put("bmp","image/bmp");
        mContentType.put("gif","image/gif");
        mContentType.put("ief","image/ief");
        mContentType.put("jpeg","image/jpeg");
        mContentType.put("jpg","image/jpeg");
        mContentType.put("jpe","image/jpeg");
        mContentType.put("png","image/png");
        mContentType.put("tiff","image/tiff");
        mContentType.put("tif","image/tiff");
        mContentType.put("djvu","image/vnd.djvu");
        mContentType.put("djv","image/vnd.djvu");
        mContentType.put("wbmp","image/vnd.wap.wbmp");
        mContentType.put("ras","image/x-cmu-raster");
        mContentType.put("pnm","image/x-portable-anymap");
        mContentType.put("pbm","image/x-portable-bitmap");
        mContentType.put("pgm","image/x-portable-graymap");
        mContentType.put("ppm","image/x-portable-pixmap");
        mContentType.put("rgb","image/x-rgb");
        mContentType.put("xbm","image/x-xbitmap");
        mContentType.put("xpm","image/x-xpixmap");
        mContentType.put("xwd","image/x-xwindowdump");
        mContentType.put("igs","model/iges");
        mContentType.put("iges","model/iges");
        mContentType.put("msh","model/mesh");
        mContentType.put("mesh","model/mesh");
        mContentType.put("silo","model/mesh");
        mContentType.put("wrl","model/vrml");
        mContentType.put("vrml","model/vrml");
        mContentType.put("css","text/css");
        mContentType.put("html","text/html");
        mContentType.put("htm","text/html");
        mContentType.put("asc","text/plain");
        mContentType.put("txt","text/plain");
        mContentType.put("rtx","text/richtext");
        mContentType.put("rtf","text/rtf");
        mContentType.put("sgml","text/sgml");
        mContentType.put("sgm","text/sgml");
        mContentType.put("tsv","text/tab-separated-values");
        mContentType.put("wml","text/vnd.wap.wml");
        mContentType.put("wmls","text/vnd.wap.wmlscript");
        mContentType.put("etx","text/x-setext");
        mContentType.put("xsl","text/xml");
        mContentType.put("xml","text/xml");
        mContentType.put("mpeg","video/mpeg");
        mContentType.put("mpg","video/mpeg");
        mContentType.put("mpe","video/mpeg");
        mContentType.put("qt","video/quicktime");
        mContentType.put("mov","video/quicktime");
        mContentType.put("mxu","video/vnd.mpegurl");
        mContentType.put("avi","video/x-msvideo");
        mContentType.put("movie","video/x-sgi-movie");
        mContentType.put("ice","x-conference/x-cooltalk");
        mContentType.put("form","application/x-www-form-urlencoded");
    }

    public static class Downloader {
        private File mFile;
        private Request.Builder mRequest;
        private OnDownloadListener mOnDownloadListener;

        public Downloader() {
            mRequest = new Request.Builder();
        }

        public Downloader url(String url) {
            mRequest.url(url);
            return this;
        }

        public Downloader get() {
            mRequest.get();
            return this;
        }

        public Downloader post() {
            post(null);
            return this;
        }

        public Downloader post(Map<String, String> params) {
            FormBody.Builder builder = new FormBody.Builder();
            if (params != null) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    if (entry.getKey() == null || entry.getKey().equals("")) {
                        continue;
                    }
                    builder.addEncoded(entry.getKey(), entry.getValue() == null ? "":entry.getValue());
                }
            }
            mRequest.post(builder.build());
            return this;
        }

        public Downloader setOnDownloadListener(OnDownloadListener listener) {
            mOnDownloadListener = listener;
            return this;
        }

        public void download() {
            if (mOnDownloadListener == null) throw new NullPointerException("OnDownloadListener must not be empty");
            if (HttpClient.getInstance().getOkHttpClient() == null) throw new NullPointerException("HttpClient has not been initialized");
            Call call = HttpClient.getInstance().getOkHttpClient().newCall(mRequest.build());
            mFile = mOnDownloadListener.onStart(call);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    mOnDownloadListener.onFailure();
                }

                @Override
                public void onResponse(Call call, Response response) {
                    try {
                        if (!response.isSuccessful()) {
                            mOnDownloadListener.onFailure();
                            return;
                        }
                        if (mFile.exists()) mFile.delete();

                        File tmp = new File(mFile.getPath() + ".tmp");
                        if (!tmp.exists()) tmp.createNewFile();
                        InputStream is = response.body().byteStream();
                        FileOutputStream fos = new FileOutputStream(tmp);

                        long now = 0l, length = response.body().contentLength();
                        int i = 0;
                        byte[] buffer = new byte[32*1024];
                        while ((i = is.read(buffer)) != -1) {
                            fos.write(buffer, 0, i);
                            now += i;
                            mOnDownloadListener.onProgress(now, length, (now + 0.0f) / length);
                        }
                        fos.close();
                        is.close();
                        tmp.renameTo(mFile);
                        mOnDownloadListener.onSucceed(mFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                        mOnDownloadListener.onFailure();
                    }
                }
            });
        }

        public interface OnDownloadListener {

            File onStart(Call call);

            void onProgress(long now, long length, float progress);

            void onSucceed(File file);

            void onFailure();
        }
    }
}