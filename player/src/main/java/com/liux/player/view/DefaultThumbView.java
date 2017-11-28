package com.liux.player.view;

import android.annotation.SuppressLint;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.liux.player.PlayerView;

/**
 * Created by Liux on 2017/10/25.
 */

@SuppressLint("ViewConstructor")
public class DefaultThumbView extends AbstractThumbView {

    public DefaultThumbView(AbstractPlayerView view) {
        super(view.getContext());
    }

    @Override
    protected ImageView initView() {
        ImageView imageView = new ImageView(getContext());
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        addView(imageView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        return imageView;
    }
}
