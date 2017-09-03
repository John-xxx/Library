package com.liux.framework_demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.liux.framework.banner.BannerAdapter;
import com.liux.framework.banner.BannerHolder;
import com.liux.framework.banner.BannerView;
import com.liux.framework.banner.DefaultIndicator;
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
    private BannerView mBannerView;

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
        mBannerView = (BannerView) findViewById(R.id.vp_banner);

        mBanners = new ArrayList<String>();
        mBannerView.setScrollerTime(800);
        mBannerView.setAdapter(new BannerAdapter<String>(mBanners, R.layout.item_banner) {
            @Override
            public void onBindData(BannerHolder holder, final String s, int position) {
                ImageView imageView = (ImageView) holder.getItemView();
                imageView.setImageResource(R.drawable.background);
                holder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(ImmerseActivity.this, "点击了Banner:" + s, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        mBannerView.setIndicator(new DefaultIndicator(this, R.drawable.indicator_bg));
    }

    @Override
    protected void onLazyLoad() {
        mBanners.add("1");
        mBanners.add("2");
        mBanners.add("3");
        mBanners.add("4");
        mBanners.add("5");
        mBannerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    protected void onRestoreData(Map<String, Object> data) {

    }

    @Override
    protected void onSaveData(Map<String, Object> data) {

    }
}
