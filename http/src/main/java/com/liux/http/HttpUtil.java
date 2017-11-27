package com.liux.http;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Http 协议配套工具类
 * Created by Liux on 2017/9/3.
 */

public class HttpUtil {
    private static final MediaType TYPE_UNKNOWN = MediaType.parse("*/*");

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
     * Android_OS-Version_packageName_versionName
     * @param context
     * @return
     */
    public static String getDefaultUserAgent(Context context) {
        String versionName = "Unknown";
        try {
            versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS).versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return String.format(
                "Android_%s_%s_%s",
                Build.VERSION.RELEASE,
                context.getPackageName(),
                versionName
        );
    }
}
