package com.liux.framework.banner;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Banner View 实现
 * Created by Liux on 2017/8/28.
 */

public class BannerView<T> extends RelativeLayout {

    private ViewPager mViewPager;
    private BannerAdapter<T> mBannerAdapter;

    public BannerView(Context context) {
        super(context);
        init();
    }

    public BannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public BannerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mViewPager = new ViewPager(getContext());
    }
}
