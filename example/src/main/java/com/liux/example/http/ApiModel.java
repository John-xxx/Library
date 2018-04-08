package com.liux.example.http;

import java.io.File;
import java.io.InputStream;

/**
 * Created by Liux on 2018/1/16.
 */

public interface ApiModel {

    void queryWeather(String code);

    void queryIP(String ip);

    void queryMobile(String mobile);

    void queryExpress(String code);

    void testTimeout(String data);

    void testTimeoutGlobal(String data);

    void testGet(int i, String s);

    void testPost(int i, String s);

    void testPostMultipart(int i, String s, File file, byte[] bytes, InputStream stream);
}
