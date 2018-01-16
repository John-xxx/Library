package com.liux.glide.video;

import android.support.annotation.Nullable;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;

import java.io.InputStream;

/**
 * Created by Liux on 2017/9/13.
 */

public class VideoModelLoader implements ModelLoader<Video, InputStream> {

    @Nullable
    @Override
    public LoadData<InputStream> buildLoadData(Video video, int width, int height, Options options) {
        return new LoadData<InputStream>(
                video,
                new VideoDataFetcher(video, width, height)
        );
    }

    @Override
    public boolean handles(Video video) {
        return true;
    }

    public static class Factory implements ModelLoaderFactory<Video, InputStream> {
        @Override
        public ModelLoader<Video, InputStream> build(MultiModelLoaderFactory multiModelLoaderFactory) {
            return new VideoModelLoader();
        }

        @Override
        public void teardown() {

        }
    }
}