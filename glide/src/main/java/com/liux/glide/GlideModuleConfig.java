package com.liux.glide;

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
import com.liux.glide.video.VideoModelLoader;
import com.liux.glide.video.VideoUrl;

import java.io.InputStream;

import okhttp3.Call;

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
     * 注册源 Model 对应的 ModelLoader
     * @param context
     * @param glide
     * @param registry
     */
    @Override
    public void registerComponents(Context context, Glide glide, Registry registry) {
        // 注册全局唯一OkHttp客户端(HttpClient初始化的情况)
        try {
            Call.Factory factory = getHttpClient();
            OkHttpUrlLoader.Factory factory_glideurl = new OkHttpUrlLoader.Factory(factory);
            registry.replace(GlideUrl.class, InputStream.class, factory_glideurl);
        } catch (Exception e) {

        }

        // 注册视频获取缩略图的扩展
        VideoModelLoader.Factory factory_videourl = new VideoModelLoader.Factory();
        registry.replace(VideoUrl.class, InputStream.class, factory_videourl);
    }

    /**
     * 清单解析是否开启 <br>
     * 要注意避免重复添加 <br>
     * @return
     */
    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }

    /**
     * 通过反射获取HttpClient实例
     * @return
     * @throws Exception
     */
    private Call.Factory getHttpClient() throws Exception {
        Class<?> clazz = Class.forName("com.liux.http.HttpClient");
        Object client = clazz.getMethod("getInstance").invoke(null);
        Object factory = client.getClass().getMethod("getOkHttpClient").invoke(client);
        return (Call.Factory) factory;
    }
}
