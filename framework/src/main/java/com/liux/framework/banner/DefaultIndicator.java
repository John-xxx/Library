package com.liux.framework.banner;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.liux.framework.util.ScreenUtil;

/**
 * 默认的指示器实现 <br>
 * 暂时没有动效,比较生硬
 * Created by Liux on 2017/9/3.
 */

public class DefaultIndicator extends LinearLayout implements Indicator {
    // 指示器大小
    private int mIndicatorSize;
    // 指示器资源
    private int mIndicatorResource;

    /**
     * 资源使用选择器标识 <br>
     * android:state_selected="true" or android:state_selected="false"
     * @param context
     * @param res
     */
    public DefaultIndicator(Context context, @DrawableRes int res) {
        super(context);
        mIndicatorResource = res;
        init();
    }

    public DefaultIndicator(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DefaultIndicator(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        mIndicatorSize = ScreenUtil.dp2px(getContext(), 8);

        setPadding(0, 0, 0, ScreenUtil.dp2px(getContext(), 20));
        setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM);
    }

    @Override
    public void onInit(BannerView bannerView, int count) {
        removeAllViews();
        for (int i = 0; i < count; i++) {
            View view = new View(getContext());
            view.setBackgroundResource(mIndicatorResource);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(mIndicatorSize, mIndicatorSize);
            lp.setMargins(mIndicatorSize / 3, 0, mIndicatorSize / 3, 0);
            view.setLayoutParams(lp);
            addView(view);
        }
        if (count > 0) {
            getChildAt(0).setSelected(true);
        }
        bannerView.addView(this, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    @Override
    public void onSelected(BannerView view, int position) {
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).setSelected(i == position);
        }
    }

    @Override
    public void onClear(BannerView view) {
        removeAllViews();
    }

    /**
     * 设置指示器资源 <br>
     * 资源使用选择器标识 <br>
     * android:state_selected="true" or android:state_selected="false"
     * @param res
     */
    public void setIndicatorResource(@DrawableRes int res) {
        mIndicatorResource = res;

        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            view.setBackgroundResource(mIndicatorResource);
        }
    }
}
