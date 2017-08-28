package com.liux.framework_demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.WindowManager;
import android.widget.ImageView;

import com.liux.framework.banner.BannerAdapter;
import com.liux.framework.banner.BannerHolder;
import com.liux.framework.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Liux on 2017/8/17.
 */

public class ImmerseActivity extends BaseActivity {
    private int mTopPadding;

    private List<String> mBanners;
    private ViewPager mViewPager;

    @Override
    protected TitleBar onInitTitleBar() {
        return new TransparentTitleBar(this) {
            @Override
            public void initView(int topPadding) {
                super.initView(topPadding);
                mTopPadding = topPadding;
            }
        };
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState, Intent intent) {
        // getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        setContentView(R.layout.activity_immerse);
    }

    @Override
    protected void onInitData(@Nullable Bundle savedInstanceState, Intent intent) {

    }

    @Override
    protected void onInitView(@Nullable Bundle savedInstanceState) {
        // findViewById(R.id.rl_root).setPadding(0, mTopPadding, 0, 0);
        mViewPager = (ViewPager) findViewById(R.id.vp_banner);

        mBanners = new ArrayList<String>();
        mViewPager.setAdapter(new BannerAdapter<String>(mBanners, R.layout.item_banner) {
            @Override
            public void onBindData(BannerHolder holder, String s, int position) {
                ImageView imageView = (ImageView) holder.getItemView();
                imageView.setImageResource(R.drawable.background);
            }
        });
        mViewPager.setCurrentItem(0x7fffffff / 2);
    }

    @Override
    protected void onLazyLoad() {
        mBanners.add("");
        mBanners.add("");
        mBanners.add("");
        mBanners.add("");
        mBanners.add("");
        mViewPager.getAdapter().notifyDataSetChanged();
    }

    @Override
    protected void onRestoreData(Map<String, Object> data) {

    }

    @Override
    protected void onSaveData(Map<String, Object> data) {

    }
}
