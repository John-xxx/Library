package com.liux.framework.glide.video;

import android.support.annotation.Nullable;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;

import java.io.InputStream;

/**
 * Created by Liux on 2017/9/13.
 */

public class VideoModelLoader implements ModelLoader<VideoUrl, InputStream> {

    @Nullable
    @Override
    public LoadData<InputStream> buildLoadData(VideoUrl videoUrl, int width, int height, Options options) {
        return new LoadData<InputStream>(
                videoUrl,
                new VideoDataFetcher(videoUrl, width, height)
        );
    }

    @Override
    public boolean handles(VideoUrl videoUrl) {
        return true;
    }

    public static class Factory implements ModelLoaderFactory<VideoUrl, InputStream> {
        @Override
        public ModelLoader<VideoUrl, InputStream> build(MultiModelLoaderFactory multiModelLoaderFactory) {
            return new VideoModelLoader();
        }

        @Override
        public void teardown() {

        }
    }
}