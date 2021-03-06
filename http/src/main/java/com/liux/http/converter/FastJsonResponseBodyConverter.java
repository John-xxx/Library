package com.liux.http.converter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.parser.ParserConfig;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;

public class FastJsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private static final Feature[] EMPTY_SERIALIZER_FEATURES = new Feature[0];

    private Type type;
    private ParserConfig config;
    private int featureValues;
    private Feature[] features;

    public FastJsonResponseBodyConverter(Type type, ParserConfig config, int featureValues, Feature... features) {
        this.type = type;
        this.config = config;
        this.featureValues = featureValues;
        this.features = features;
    }

    @Override
    public T convert(ResponseBody responseBody) throws IOException {
        T t;
        try {
            t = JSON.parseObject(
                    responseBody.string(),
                    this.type,
                    this.config,
                    this.featureValues,
                    this.features != null ? this.features : EMPTY_SERIALIZER_FEATURES
            );
        } finally {
            responseBody.close();
        }

        return t;
    }
}