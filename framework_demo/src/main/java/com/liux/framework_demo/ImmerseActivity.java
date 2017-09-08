package com.liux.framework_demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.liux.framework.banner.BannerAdapter;
import com.liux.framework.banner.BannerHolder;
import com.liux.framework.banner.BannerView;
import com.liux.framework.banner.DefaultIndicator;
import com.liux.framework.base.BaseActivity;
import com.liux.framework.glide.GlideApp;
import com.liux.framework.other.CountDownTimer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Liux on 2017/8/17.
 */

public class ImmerseActivity extends BaseActivity {
    private static final String TAG = "ImmerseActivity";
    private int mTopPadding;

    private List<String> mBanners;
    private BannerView mBannerView;
    private BannerAdapter<String> mBannerAdapter;

    private Button mSend;
    private CountDownTimer mCountDownTimer;

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
        mCountDownTimer = new CountDownTimer.Builder()
                .gross(60 * 1000)
                .interval(1000)
                .listener(new CountDownTimer.OnTimerListener() {
                    @Override
                    public void onReset(int requestCode) {
                        mSend.setText("重新发送");
                    }

                    @Override
                    public void onTick(int requestCode, long surplus) {
                        Log.d(TAG, String.valueOf(surplus));
                        // 2999 毫秒应该显示为 3 秒
                        if (surplus % 1000 > 1000 / 2) {
                            surplus = (surplus / 1000 + 1) * 1000;
                        }
                        mSend.setText(String.format("%ds后重新发送", surplus / 1000));
                    }

                    @Override
                    public void onFinish(int requestCode) {
                        mSend.setText("重新发送");
                    }
                })
                .build();
    }

    @Override
    protected void onInitView(@Nullable Bundle savedInstanceState) {
        // findViewById(R.id.rl_root).setPadding(0, mTopPadding, 0, 0);
        mSend = (Button) findViewById(R.id.btn_send);
        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCountDownTimer.start();
            }
        });

        mBannerView = (BannerView) findViewById(R.id.vp_banner);

        mBanners = new ArrayList<String>();
        mBannerView.setScrollerTime(400);
        mBannerAdapter = new BannerAdapter<String>(mBanners, R.layout.item_banner) {
            @Override
            public void onBindData(BannerHolder holder, final String s, int index) {
                ImageView imageView = (ImageView) holder.getItemView();
                if (s == null && s.length() == 0) {
                    imageView.setImageResource(R.drawable.background);
                } else {
                    GlideApp.with(imageView.getContext())
                            .asBitmap()
                            .load(s)
                            .into(imageView);
                }
                holder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(ImmerseActivity.this, "点击了Banner:" + s, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };
        mBannerView.setAdapter(mBannerAdapter);
        mBannerView.setIndicator(new DefaultIndicator(this, R.drawable.indicator_bg));
    }

    @Override
    protected void onLazyLoad() {
        Observable.fromArray(
                "http://i1.s2.dpfile.com/pc/ee1f5ee79a4683619b26a8a795da2990(700x700)/thumb.jpg",
                "http://pic5.qiyipic.com/common/20130524/7dc5679567cd4243a0a41e5bf626ad77.jpg",
                "http://f.hiphotos.baidu.com/zhidao/pic/item/8b82b9014a90f60326b707453b12b31bb051eda9.jpg"
        )
                .delay(1000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable disposable) {

                    }

                    @Override
                    public void onNext(@NonNull String s) {
                        mBanners.add(s);
                    }

                    @Override
                    public void onError(@NonNull Throwable throwable) {

                    }

                    @Override
                    public void onComplete() {
                        mBannerAdapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    protected void onRestoreData(Map<String, Object> data) {

    }

    @Override
    protected void onSaveData(Map<String, Object> data) {

    }
}
