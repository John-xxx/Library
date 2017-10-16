package com.liux.boxing;

import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.bilibili.boxing.loader.IBoxingCallback;
import com.bilibili.boxing.loader.IBoxingMediaLoader;


public class BoxingGlideLoader implements IBoxingMediaLoader {

    @Override
    public void displayThumbnail(@NonNull ImageView img, @NonNull String absPath, int width, int height) {
//        String path = "file://" + absPath;
//        GlideApp.with(img.getContext())
//                .load(path)
//                .placeholder(R.drawable.ic_boxing_default_image)
//                .transition(new DrawableTransitionOptions().crossFade())
//                .centerCrop()
//                .override(width, height)
//                .into(img);
    }

    @Override
    public void displayRaw(@NonNull final ImageView img, @NonNull String absPath, int width, int height, final IBoxingCallback callback) {
//        String path = "file://" + absPath;
//        GlideRequest<Bitmap> request = GlideApp.with(img.getContext())
//                .asBitmap()
//                .load(path);
//        if (width > 0 && height > 0) {
//            request.override(width, height);
//        }
//        request.listener(new RequestListener<Bitmap>() {
//            @Override
//            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
//                if (callback != null) {
//                    callback.onFail(e);
//                    return true;
//                }
//                return false;
//            }
//
//            @Override
//            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
//                if (resource != null && callback != null) {
//                    img.setImageBitmap(resource);
//                    callback.onSuccess();
//                    return true;
//                }
//                return false;
//            }
//        }).into(img);
    }
}
