package com.liux.framework.tool;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.ExternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;

import java.io.InputStream;

/**
 * Glide全局配置 <br>
 * 定义缓存参数 <br>
 * 使用全局OkHttpClient <br>
 * Created by Liux on 2017/7/18.
 */

@GlideModule
public class GlideModuleConfig extends AppGlideModule {

    @Override
    public void applyOptions(Context context, GlideBuilder glideBuilder) {
        /* 自定义内存和图片池大小 */
        // 取1/8最大内存作为最大缓存
        int memorySize = (int) (Runtime.getRuntime().maxMemory()) / 8;
        glideBuilder.setMemoryCache(new LruResourceCache(memorySize));
        glideBuilder.setBitmapPool(new LruBitmapPool(memorySize));

        /* 定义SD卡缓存大小和位置 */
        int diskSize = 1024 * 1024 * 100;
        // ExternalCacheDiskCacheFactory
        // /sdcard/Android/data/<application package>/cache
        glideBuilder.setDiskCache(new ExternalCacheDiskCacheFactory(context, "glide", diskSize));

        /* 默认内存和图片池大小 */
        MemorySizeCalculator calculator = new MemorySizeCalculator.Builder(context).build();
        // 默认内存大小
        int defaultMemoryCacheSize = calculator.getMemoryCacheSize();
        // 默认图片池大小
        int defaultBitmapPoolSize = calculator.getBitmapPoolSize();
        // 该两句无需设置，是默认的
        glideBuilder.setMemoryCache(new LruResourceCache(defaultMemoryCacheSize));
        glideBuilder.setBitmapPool(new LruBitmapPool(defaultBitmapPoolSize));

        /* 定义图片格式 */
        glideBuilder.setDefaultRequestOptions(new RequestOptions()
                .centerCrop()
                .format(DecodeFormat.PREFER_RGB_565));
    }

    /**
     * 配置全局唯一OkHttp客户端(HttpClient初始化的情况)
     * @param context
     * @param glide
     * @param registry
     */
    @Override
    public void registerComponents(Context context, Glide glide, Registry registry) {
        OkHttpUrlLoader.Factory factory;
        try {
            factory = new OkHttpUrlLoader.Factory(HttpClient.getInstance().getOkHttpClient());
        } catch (Exception e) {
            factory = new OkHttpUrlLoader.Factory();
        }
        registry.append(GlideUrl.class, InputStream.class, factory);
    }

    /**
     * 清单解析的开启 <br>
     * 要注意避免重复添加 <br>
     * @return
     */
    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }
}
