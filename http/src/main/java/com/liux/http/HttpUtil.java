package com.liux.http;

import android.content.Context;
import android.content.pm.PackageManager;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.internal.http.HttpMethod;

/**
 * Http 协议配套工具类
 * Created by Liux on 2017/9/3.
 */

public class HttpUtil {
    private static final MediaType TYPE_UNKNOWN = MediaType.parse("*/*");

    /**
     * 查询某字符串是否是HTTP请求方法(支持HTTP/1.1)
     * @param method
     * @return
     */
    public static boolean isHttpMethod(String method) {
        if (method == null || method.isEmpty()) return false;
        switch (method.toUpperCase()) {
            // HTTP/0.9
            case "GET":
            // HTTP/1.0
            case "HEAD":
            case "POST":
            // HTTP/1.1
            case "PUT":
            case "CONNECT":
            case "TRACE":
            case "OPTIONS":
            case "DELETE":
                return true;
            default:
                return false;
        }
    }

    /**
     * 不需要请求体的方法
     * @param method
     * @return
     */
    public static boolean notRequiresRequestBody(String method) {
        return !HttpMethod.permitsRequestBody(method)
                || method.equals("GET")
                || method.equals("HEAD")
                || method.equals("TRACE")
                || method.equals("CONNECT");
    }

    /**
     * 需要请求体的方法(DELETE可以为空)
     * @param method
     * @return
     */
    public static boolean requiresRequestBody(String method) {
        return HttpMethod.requiresRequestBody(method);
    }

    /**
     * 允许有请求体的方法
     * @param method
     * @return
     */
    public static boolean permitsRequestBody(String method) {
        return HttpMethod.permitsRequestBody(method);
    }

    /**
     * 根据文件后缀名解析类型
     * @param file
     * @return
     */
    public static MediaType getMimeType(File file) {
        if (file == null) return TYPE_UNKNOWN;

        return getMimeType(file.getName());
    }

    /**
     * 根据文件后缀名解析类型
     * @param filename
     * @return
     */
    public static MediaType getMimeType(String filename) {
        if (filename == null) return TYPE_UNKNOWN;

        String[] ss = filename.split("\\.");
        if (ss.length < 2) return TYPE_UNKNOWN;

        String suffix = ss[ss.length - 1];
        String type = MimeUtils.guessMimeTypeFromExtension(suffix);

        if (type == null) return TYPE_UNKNOWN;
        return MediaType.parse(type);
    }

    /**
     * 根据媒体类型解析后缀
     * @param type
     * @return
     */
    public static String getMimeSuffix(MediaType type) {
        if (type == null) return "";

        return getMimeSuffix(type.toString());
    }

    /**
     * 根据媒体类型解析后缀
     * @param type
     * @return
     */
    public static String getMimeSuffix(String type) {
        if (type == null) return "";

        String suffix = MimeUtils.guessExtensionFromMimeType(type);

        if (suffix != null) return suffix;
        return "";
    }

    /**
     * 是否是文本型媒体
     * @param type
     * @return
     */
    public static boolean isTextMediaType(MediaType type) {
        String t2 = type.subtype();
        String t1 = type.type();
        String t = t1 + t2;
        return "text".equals(t1)
                || "application/json".equals(t);
    }

    /**
     * 生成一个XML请求体
     * @param content
     * @return
     */
    public static RequestBody parseXml(String content) {
        return parseRaw("text/xml;charset=UTF-8", content);
    }

    /**
     * 生成一个JSON请求体
     * @param content
     * @return
     */
    public static RequestBody parseJson(String content) {
        return parseRaw("application/json;charset=UTF-8", content);
    }

    /**
     * 生成一个指定类型请求体
     * @param content
     * @return
     */
    public static RequestBody parseRaw(String type, String content) {
        MediaType mediaType = MediaType.parse(type);
        return RequestBody.create(mediaType, content);
    }

    /**
     * 生成一个 {@link MultipartBody.Part}
     * @param key
     * @param file
     * @return
     */
    public static MultipartBody.Part parsePart(String key, File file) {
        MediaType mediaType = getMimeType(file);
        RequestBody body = RequestBody.create(mediaType, file);
        return MultipartBody.Part.createFormData(key, file.getName(), body);
    }

    /**
     * 字符转JSON
     * @param s
     * @return
     */
    public static String string2Json(String s) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '\"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '/':
                    sb.append("\\/");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    sb.append(c);
                    break;
            }
        }
        return sb.toString();
    }

    /**
     * JSON转字符
     * @param json
     * @return
     */
    public static String json2String(String json) {
        if (json.indexOf('"') == 0) json = json.substring(1);
        if (json.lastIndexOf('"') == json.length() - 1) json = json.substring(0, json.length() - 1);
        return json
                .replace("\\\"", "\"")
                .replace("\\\\", "\\")
                .replace("\\/", "/")
                .replace("\\b", "\b")
                .replace("\\f", "\f")
                .replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\t", "\t");
    }

    /**
     * Dalvik/2.1.0 (Linux; U; Android 6.0.1; MI 4LTE MIUI/7.11.9) App_packageName_versionCode
     * @param context
     * @return
     */
    public static String getDefaultUserAgent(Context context) {
        // Mozilla/5.0 (Linux; Android 6.0.1; MI 4LTE Build/MMB29M; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/57.0.2987.132 Mobile Safari/537.36
        // WebSettings.getDefaultUserAgent(context);
        // Dalvik/2.1.0 (Linux; U; Android 6.0.1; MI 4LTE MIUI/7.11.9)
        // System.getProperty("http.agent");

        int versionCode = -1;
        try {
            versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS).versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return String.format(
                Locale.CHINA,
                "%s App_%s_%d",
                System.getProperty("http.agent"),
                context.getPackageName(),
                versionCode
        );
    }

    /**
     * OkHttp 请求头不能是 null/换行符/中文 等一些字符
     * @param text
     * @return
     */
    public static String checkChar(String text) {
        if (text == null) return "";
        String newValue = text.replace("\n", "");
        for (int i = 0, length = newValue.length(); i < length; i++) {
            char c = newValue.charAt(i);
            if (c <= '\u001f' || c >= '\u007f') {
                try {
                    return URLEncoder.encode(newValue, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    break;
                }
            }
        }
        return newValue;
    }
}
