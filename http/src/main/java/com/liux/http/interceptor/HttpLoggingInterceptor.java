/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.liux.http.interceptor;

import android.util.Log;

import com.liux.http.Http;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.EOFException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import okhttp3.Connection;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpHeaders;
import okio.Buffer;
import okio.BufferedSource;

/**
 * An OkHttp interceptor which logs request and response information. Can be applied as an
 * {@linkplain OkHttpClient#interceptors() application interceptor} or as a {@linkplain
 * OkHttpClient#networkInterceptors() network interceptor}. <p> The format of the logs created by
 * this class should not be considered stable and may change slightly between releases. If you need
 * a stable logging format, use your own interceptor.
 */
public class HttpLoggingInterceptor implements Interceptor {
    private static final int MAX_LOG_LENGTH = 10000;
    private static final Charset UTF8 = Charset.forName("UTF-8");
    private static final String SEPARATOR = System.getProperty("line.separator");
    private static final String DIVIDE_0 = "┃";
    private static final String DIVIDE_1;
    private static final String DIVIDE_2;
    private static final String DIVIDE_3;

    static {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 200; i++) {
            builder.append('━');
        }
        DIVIDE_1 = "┏" + builder.toString();
        DIVIDE_2 = "┣" + builder.toString();
        DIVIDE_3 = "┗" + builder.toString();
    }

    public enum Level {
        /** No logs. */
        NONE,
        /**
         * Logs request and response lines.
         *
         * <p>Example:
         * <pre>{@code
         * --> POST /greeting http/1.1 (3-byte body)
         *
         * <-- 200 OK (22ms, 6-byte body)
         * }</pre>
         */
        BASIC,
        /**
         * Logs request and response lines and their respective headers.
         *
         * <p>Example:
         * <pre>{@code
         * --> POST /greeting http/1.1
         * Host: example.com
         * Content-Type: plain/text
         * Content-Length: 3
         * --> END POST
         *
         * <-- 200 OK (22ms)
         * Content-Type: plain/text
         * Content-Length: 6
         * <-- END HTTP
         * }</pre>
         */
        HEADERS,
        /**
         * Logs request and response lines and their respective headers and bodies (if present).
         *
         * <p>Example:
         * <pre>{@code
         * --> POST /greeting http/1.1
         * Host: example.com
         * Content-Type: plain/text
         * Content-Length: 3
         *
         * Hi?
         * --> END POST
         *
         * <-- 200 OK (22ms)
         * Content-Type: plain/text
         * Content-Length: 6
         *
         * Hello!
         * <-- END HTTP
         * }</pre>
         */
        BODY
    }

    public interface Logger {
        void log(String message);

        /** A {@link HttpLoggingInterceptor.Logger} defaults output appropriate for the current platform. */
        HttpLoggingInterceptor.Logger DEFAULT = new HttpLoggingInterceptor.Logger() {
            @Override public void log(String message) {
                //Platform.get().log(INFO, message, null);
                for (int i = 0, length = message.length(); i < length; i++) {
                    int newline = message.indexOf(SEPARATOR, i);
                    newline = newline != -1 ? newline : length;
                    do {
                        int end = Math.min(newline, i + MAX_LOG_LENGTH);
                        String lineStr = message.substring(i, end);
                        if (!DIVIDE_1.equals(lineStr) && !DIVIDE_2.equals(lineStr) && !DIVIDE_3.equals(lineStr)) {
                            lineStr = DIVIDE_0 + lineStr;
                        }
                        Log.println(Log.DEBUG, Http.TAG, lineStr.replace(SEPARATOR, ""));
                        i = end;
                    } while (i < newline);
                }
            }
        };
    }

    public HttpLoggingInterceptor() {
        this(HttpLoggingInterceptor.Logger.DEFAULT);
    }

    public HttpLoggingInterceptor(HttpLoggingInterceptor.Logger logger) {
        this.logger = logger;
    }

    private final HttpLoggingInterceptor.Logger logger;

    private volatile HttpLoggingInterceptor.Level level = HttpLoggingInterceptor.Level.NONE;

    /** Change the level at which this interceptor logs. */
    public HttpLoggingInterceptor setLevel(HttpLoggingInterceptor.Level level) {
        if (level == null) throw new NullPointerException("level == null. Use Level.NONE instead.");
        this.level = level;
        return this;
    }

    public HttpLoggingInterceptor.Level getLevel() {
        return level;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        HttpLoggingInterceptor.Level level = this.level;

        Request request = chain.request();
        if (level == HttpLoggingInterceptor.Level.NONE) {
            return chain.proceed(request);
        }

        StringBuffer stringBuffer = new StringBuffer();
        addLog(stringBuffer, DIVIDE_1);

        boolean logBody = level == HttpLoggingInterceptor.Level.BODY;
        boolean logHeaders = logBody || level == HttpLoggingInterceptor.Level.HEADERS;

        RequestBody requestBody = request.body();
        boolean hasRequestBody = requestBody != null;

        Connection connection = chain.connection();
        String requestStartMessage = "--> "
                + request.method()
                + ' ' + request.url()
                + (connection != null ? " " + connection.protocol() : "");
        if (!logHeaders && hasRequestBody) {
            requestStartMessage += " (" + requestBody.contentLength() + "-byte body)";
        }
        //logger.log(requestStartMessage);
        addLog(stringBuffer, requestStartMessage);

        if (logHeaders) {
            if (hasRequestBody) {
                // Request body headers are only present when installed as a network interceptor. Force
                // them to be included (when available) so there values are known.
                if (requestBody.contentType() != null) {
                    //logger.log("Content-Type: " + requestBody.contentType());
                    addLog(stringBuffer, "Content-Type: " + requestBody.contentType());
                }
                if (requestBody.contentLength() != -1) {
                    //logger.log("Content-Length: " + requestBody.contentLength());
                    addLog(stringBuffer, "Content-Length: " + requestBody.contentLength());
                }
            }

            Headers headers = request.headers();
            for (int i = 0, count = headers.size(); i < count; i++) {
                String name = headers.name(i);
                // Skip headers from the request body as they are explicitly logged above.
                if (!"Content-Type".equalsIgnoreCase(name) && !"Content-Length".equalsIgnoreCase(name)) {
                    //logger.log(name + ": " + headers.value(i));
                    addLog(stringBuffer, name + ": " + headers.value(i));
                }
            }

            if (!logBody || !hasRequestBody) {
                //logger.log("--> END " + request.method());
                addLog(stringBuffer, "--> END " + request.method());
            } else if (bodyEncoded(request.headers())) {
                //logger.log("--> END " + request.method() + " (encoded body omitted)");
                addLog(stringBuffer, "--> END " + request.method() + " (encoded body omitted)");
            } else {
                Buffer buffer = new Buffer();
                requestBody.writeTo(buffer);

                Charset charset = UTF8;
                MediaType contentType = requestBody.contentType();
                if (contentType != null) {
                    charset = contentType.charset(UTF8);
                }

                //logger.log("");
                addLog(stringBuffer, "");
                if (isPlaintext(buffer)) {
                    //logger.log(buffer.readString(charset));
                    addLog(stringBuffer, buffer.readString(charset));
                    //logger.log("--> END " + request.method()
                    //        + " (" + requestBody.contentLength() + "-byte body)");
                    addLog(stringBuffer, "--> END " + request.method()
                            + " (" + requestBody.contentLength() + "-byte body)");
                } else {
                    //logger.log("--> END " + request.method() + " (binary "
                    //        + requestBody.contentLength() + "-byte body omitted)");
                    addLog(stringBuffer, "--> END " + request.method() + " (binary "
                            + requestBody.contentLength() + "-byte body omitted)");
                }
            }
        }

        long startNs = System.nanoTime();
        Response response;

        addLog(stringBuffer, DIVIDE_2);

        try {
            response = chain.proceed(request);
        } catch (Exception e) {
            //logger.log("<-- HTTP FAILED: " + e);
            addLog(stringBuffer, "<-- HTTP FAILED: " + e);
            addLog(stringBuffer, DIVIDE_3);
            printLog(stringBuffer);
            throw e;
        }
        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);

        ResponseBody responseBody = response.body();
        long contentLength = responseBody.contentLength();
        String bodySize = contentLength != -1 ? contentLength + "-byte" : "unknown-length";
        //logger.log("<-- "
        //        + response.code()
        //        + (response.message().isEmpty() ? "" : ' ' + response.message())
        //        + ' ' + response.request().url()
        //        + " (" + tookMs + "ms" + (!logHeaders ? ", " + bodySize + " body" : "") + ')');
        addLog(stringBuffer, "<-- "
                + response.code()
                + (response.message().isEmpty() ? "" : ' ' + response.message())
                + ' ' + response.request().url()
                + " (" + tookMs + "ms" + (!logHeaders ? ", " + bodySize + " body" : "") + ')');

        if (logHeaders) {
            Headers headers = response.headers();
            for (int i = 0, count = headers.size(); i < count; i++) {
                //logger.log(headers.name(i) + ": " + headers.value(i));
                addLog(stringBuffer, headers.name(i) + ": " + headers.value(i));
            }

            if (!logBody || !HttpHeaders.hasBody(response)) {
                //logger.log("<-- END HTTP");
                addLog(stringBuffer, "<-- END HTTP");
            } else if (bodyEncoded(response.headers())) {
                //logger.log("<-- END HTTP (encoded body omitted)");
                addLog(stringBuffer, "<-- END HTTP (encoded body omitted)");
            } else {
                BufferedSource source = responseBody.source();
                source.request(Long.MAX_VALUE); // Buffer the entire body.
                Buffer buffer = source.buffer();

                Charset charset = UTF8;
                MediaType contentType = responseBody.contentType();
                if (contentType != null) {
                    charset = contentType.charset(UTF8);
                }

                if (!isPlaintext(buffer)) {
                    //logger.log("");
                    addLog(stringBuffer, "");
                    //logger.log("<-- END HTTP (binary " + buffer.size() + "-byte body omitted)");
                    addLog(stringBuffer, "<-- END HTTP (binary " + buffer.size() + "-byte body omitted)");
                    return response;
                }

                if (contentLength != 0) {
                    //logger.log("");
                    addLog(stringBuffer, "");
                    //logger.log(buffer.clone().readString(charset));
                    addLog(stringBuffer, buffer.clone().readString(charset));
                }

                //logger.log("<-- END HTTP (" + buffer.size() + "-byte body)");
                addLog(stringBuffer, "<-- END HTTP (" + buffer.size() + "-byte body)");
            }
        }

        addLog(stringBuffer, DIVIDE_3);
        printLog(stringBuffer);

        return response;
    }

    /**
     * Returns true if the body in question probably contains human readable text. Uses a small sample
     * of code points to detect unicode control characters commonly used in binary file signatures.
     */
    static boolean isPlaintext(Buffer buffer) {
        try {
            Buffer prefix = new Buffer();
            long byteCount = buffer.size() < 64 ? buffer.size() : 64;
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 16; i++) {
                if (prefix.exhausted()) {
                    break;
                }
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }
            return true;
        } catch (EOFException e) {
            return false; // Truncated UTF-8 sequence.
        }
    }

    private boolean bodyEncoded(Headers headers) {
        String contentEncoding = headers.get("Content-Encoding");
        return contentEncoding != null && !contentEncoding.equalsIgnoreCase("identity");
    }

    private void addLog(StringBuffer stringBuffer, String log) {
        log = formatJson(log);
        log = formatXml(log);
        stringBuffer.append(log).append(SEPARATOR);
    }

    private void printLog(StringBuffer stringBuffer) {
        logger.log(stringBuffer.toString());
    }

    /**
     * 格式化 Json
     * @param json
     * @return
     */
    private static String formatJson(String json) {
        try {
            String check = json.trim();
            if (check.startsWith("{") && check.endsWith("}")) {
                json = new JSONObject(json).toString(4);
            } else if (check.startsWith("[") && check.endsWith("]")) {
                json = new JSONArray(json).toString(4);
            }
        } catch (JSONException e) {

        }
        return json;
    }

    /**
     * 格式化 Xml
     * @param xml
     * @return
     */
    private static String formatXml(String xml) {
        try {
            String check = xml.trim();
            if (!check.startsWith("<") || !check.endsWith(">")) {
                return xml;
            }
            Source xmlInput = new StreamSource(new StringReader(xml));
            StreamResult xmlOutput = new StreamResult(new StringWriter());
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.transform(xmlInput, xmlOutput);
            xml = xmlOutput.getWriter().toString().replaceFirst(">", ">" + SEPARATOR);
        } catch (Exception e) {

        }
        return xml;
    }
}
