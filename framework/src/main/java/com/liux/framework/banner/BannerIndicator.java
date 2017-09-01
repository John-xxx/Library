package com.liux.framework.banner;

import android.widget.RelativeLayout;

/**
 * Created by Liux on 2017/9/1.
 */

public interface BannerIndicator {

    void onInitIndicator(BannerView view, int count);

    void onSelected(BannerView view, int position);
}
