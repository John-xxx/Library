package com.liux.http.interceptor;

import android.text.TextUtils;

import com.liux.http.HttpClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 根据特定Header更换请求BaseUrl
 * 作用域 BASE_URL > BASE_URL_RULE > BaseUrl
 * 1.动态更换Scheme       支持
 * 2.动态更换Host         支持
 * 3.动态更换Port         支持
 * 4.动态更换Authority    不需要支持
 * 5.动态更换Path         支持(BaseUrl中部分)
 * 6.动态更换QueryData    不需要支持
 * 7.动态更换Fragment     不需要支持
 * Created by Liux on 2018/1/16.
 */

public class BaseUrlInterceptor implements Interceptor {
    // 自定义头部信息,标记Api动态域名信息
    public static final String BASE_URL = "Domain-Name";
    // 自定义头部信息,标记Api动态域名规则信息
    public static final String BASE_URL_RULE = "Domain-Rule";

    private HttpClient mHttpClient;
    // 全局动态BaseUrl
    private String mBaseUrl = null;
    // 动态域名规则
    private Map<String, String> mDomainRules = new HashMap<>();

    public BaseUrlInterceptor(HttpClient httpClient) {
        mHttpClient = httpClient;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        String url = null;

        // 检测有没有 BASE_URL
        url = request.header(BASE_URL);
        if (!TextUtils.isEmpty(url)) {
            request = parseNewRequest(url, request, BASE_URL);
            return chain.proceed(request);
        }

        // 检测有没有 BASE_URL_RULE
        String rule = request.header(BASE_URL_RULE);
        if (!TextUtils.isEmpty(rule)) {
            url = getDomainRule(rule);
            if (!TextUtils.isEmpty(url)) {
                request = parseNewRequest(url, request, BASE_URL_RULE);
                return chain.proceed(request);
            }
        }

        // 检测全局 BaseUrl
        url = getBaseUrl();
        if (!equalsRetorfitBaseUrl(url)) {
            request = parseNewRequest(url, request, null);
            return chain.proceed(request);
        }

        return chain.proceed(request);
    }

    /**
     * 获取当前全局BaseUrl
     * @return
     */
    public String getBaseUrl() {
        return mBaseUrl;
    }

    /**
     * 设置当前全局BaseUrl
     * @param baseUrl
     * @return
     */
    public void setBaseUrl(String baseUrl) {
        mBaseUrl = baseUrl;
    }

    /**
     * 获取某个规则对应的URL
     * @param rule
     * @return
     */
    public String getDomainRule(String rule) {
        String url = mDomainRules.get(rule);
        return url;
    }

    /**
     * 加入某个URL对应的规则
     * @param rule
     * @param baseUrl
     * @return
     */
    public void putDomainRule(String rule, String baseUrl) {
        mDomainRules.put(rule, baseUrl);
    }

    /**
     * 获取所有URL对应规则,Copy目的是防止跳过检查添加规则
     * @return
     */
    public Map<String, String> getDomainRules() {
        Map<String, String> result = new HashMap<>();
        result.putAll(mDomainRules);
        return result;
    }

    /**
     * 清除所有URL对应规则
     * @return
     */
    public void clearDomainRules() {
        mDomainRules.clear();
    }

    /**
     * 复制原始请求体并更换Host
     * @param domain
     * @param request
     * @return
     */
    private Request parseNewRequest(String domain, Request request, String header) {
        return request.newBuilder()
                .url(parseNewHttpUrl(domain, request))
                .removeHeader(header != null ? header : "")
                .build();
    }

    /**
     * 复制原始请求URL并替换BaseUrl信息
     * @param url
     * @param request
     * @return
     */
    private HttpUrl parseNewHttpUrl(String url, Request request) {
        // 用BASE_URL的时候无法限制BaseUrl格式所以加上判断
        if (!url.endsWith("/")) url += "/";

        HttpUrl reqHttpUrl = request.url();
        HttpUrl oldHttpUrl = mHttpClient.getRetrofit().baseUrl();

        String reqUrl = reqHttpUrl.url().toString();
        String oldUrl = oldHttpUrl.url().toString();

        String newUrl;
        // 当Service中路径以"/"开头表示从根路径开始的路径
        if (reqUrl.startsWith(oldUrl)) {
            newUrl =  reqUrl.replace(oldUrl, url);
        } else {
            newUrl = reqUrl.replace(
                    oldUrl.substring(0, oldUrl.indexOf("/", 10)),
                    url.substring(0, url.indexOf("/", 10))
            );
        }

        HttpUrl.Builder builder = request.url().newBuilder(newUrl)
                .encodedUsername(reqHttpUrl.username())
                .encodedPassword(reqHttpUrl.password());
        for (String key : reqHttpUrl.queryParameterNames()) {
            List<String> values = reqHttpUrl.queryParameterValues(key);
            for (String value : values) {
                builder.addEncodedQueryParameter(key, value);
            }
        }
        builder.encodedFragment(reqHttpUrl.fragment());

        return builder.build();
    }

    /**
     * 比对新的Url和Retorfit中的BaseUrl是否一致
     * @param url
     * @return
     */
    private boolean equalsRetorfitBaseUrl(String url) {
        String oldUrl = mHttpClient.getRetrofit().baseUrl().url().toString();
        return oldUrl.equals(url);
    }
}
