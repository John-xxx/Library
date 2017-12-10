package com.liux.downloader.util;

import android.text.TextUtils;

import com.liux.downloader.Task;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Liux on 2017/12/10.
 */

public class TaskUtil {

    public static Task creatorTask(String url, File dir, Map<String, String> header) {
        if (TextUtils.isEmpty(url)) throw new NullPointerException("The url cannot be empty");

        if (dir == null) throw new NullPointerException("The url cannot be empty");
        if (!dir.exists()) dir.mkdirs();
        if (dir.exists() && !dir.isDirectory()) throw new NullPointerException("The url cannot be empty");
        String dirString = dir.getAbsolutePath();
        if (dirString.lastIndexOf(File.separator) != dirString.length()) dirString = dirString + File.separator;

        String headerString = encodeHeader(header);

        return new Task(url, dirString, headerString);
    }

    /**
     * 编码自定义请求头
     * @param header
     * @return
     */
    public static String encodeHeader(Map<String, String> header) {
        if (header == null || header.isEmpty()) return null;
        List<Map.Entry<String,String>> entrys = new ArrayList<>();
        entrys.addAll(header.entrySet());
        Collections.sort(entrys, new Comparator<Map.Entry<String,String>>(){
            @Override
            public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                return o1.getKey().compareTo(o2.getKey());
            }
        });

        StringBuilder builder = new StringBuilder();
        for(Map.Entry<String,String> entry : entrys){
            builder
                    .append(entry.getKey())
                    .append(entry.getValue());
        }
        return builder.toString();
    }

    /**
     * 解码自定义请求头
     * @param headerString
     * @return
     */
    public static Map<String, String> decodeHeader(String headerString) {
        Map<String, String> header = new IdentityHashMap<>();
        if (TextUtils.isEmpty(headerString)) return header;

        return header;
    }
}
