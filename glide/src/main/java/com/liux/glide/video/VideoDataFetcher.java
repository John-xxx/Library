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

    private Video mVideo;
    private int mWidth, mHeight;

    private MediaMetadataRetriever mMediaMetadataRetriever;

    public VideoDataFetcher(Video video, int width, int height) {
        mVideo = video;
        mWidth = width;
        mHeight = height;
    }

    @Override
    public void loadData(Priority priority, DataCallback<? super InputStream> dataCallback) {
        String url = mVideo.getStringUrl();

        MediaMetadataRetriever retriever = getMediaMetadataRetriever();
        try {
            if (Build.VERSION.SDK_INT >= 14) {
                retriever.setDataSource(url, new HashMap<String, String>());
            } else {
                retriever.setDataSource(url);
            }

            Bitmap bitmap = retriever.getFrameAtTime();
            if (bitmap != null) {
                dataCallback.onLoadFailed(new NullPointerException("gets thumbnail failure."));
                return;
            }

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

    private MediaMetadataRetriever getMediaMetadataRetriever() {
        if (mMediaMetadataRetriever == null) {
            mMediaMetadataRetriever = new MediaMetadataRetriever();
        }
        return mMediaMetadataRetriever;
    }

    @Override
    public void cleanup() {
        if (getMediaMetadataRetriever() != null) {
            getMediaMetadataRetriever().release();
            mMediaMetadataRetriever = null;
        }
    }

    @Override
    public void cancel() {
        if (getMediaMetadataRetriever() != null) {
            getMediaMetadataRetriever().release();
            mMediaMetadataRetriever = null;
        }
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