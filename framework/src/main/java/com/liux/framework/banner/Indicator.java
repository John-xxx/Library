package com.liux.framework.banner;

/**
 * Created by Liux on 2017/9/1.
 */

public interface Indicator {

    void onInit(BannerView view, int count);

    void onSelected(BannerView view, int position);

    void onClear(BannerView view);
}
