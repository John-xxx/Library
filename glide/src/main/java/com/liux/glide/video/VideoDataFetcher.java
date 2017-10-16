package com.liux.glide.video;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Build;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.data.DataFetcher;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;

/**
 * Created by Liux on 2017/9/13.
 */

public class VideoDataFetcher implements DataFetcher<InputStream> {

    private VideoUrl mVideoUrl;
    private int mWidth, mHeight;

    public VideoDataFetcher(VideoUrl videoUrl, int width, int height) {
        mVideoUrl = videoUrl;
        mWidth = width;
        mHeight = height;
    }

    @Override
    public void loadData(Priority priority, DataCallback<? super InputStream> dataCallback) {
        String url = mVideoUrl.getStringUrl();

        MediaMetadataRetriever retriever = null;
        try {
            retriever = new MediaMetadataRetriever();
            if (Build.VERSION.SDK_INT >= 14) {
                retriever.setDataSource(url, new HashMap<String, String>());
            } else {
                retriever.setDataSource(url);
            }

            Bitmap bitmap = retriever.getFrameAtTime();

//            // 计算缩略图
//            // 已取消,交给Glide自行处理
//            if (bitmap != null) {
//                bitmap = ThumbnailUtils.extractThumbnail(bitmap, mWidth, mHeight);
//            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
            InputStream inputStream = new ByteArrayInputStream(baos.toByteArray());

            dataCallback.onDataReady(inputStream);
        } catch (Exception e) {
            dataCallback.onLoadFailed(e);
        } finally {
            try {
                if (retriever != null) retriever.release();
            } catch (Exception e) {

            }
        }
    }

    @Override
    public void cleanup() {

    }

    @Override
    public void cancel() {

    }

    @Override
    public Class<InputStream> getDataClass() {
        return InputStream.class;
    }

    @Override
    public DataSource getDataSource() {
        return DataSource.REMOTE;
    }
}