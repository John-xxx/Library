package com.liux.example;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.liux.banner.BannerAdapter;
import com.liux.banner.BannerHolder;
import com.liux.banner.BannerView;
import com.liux.banner.DefaultIndicator;
import com.liux.base.BaseActivity;
import com.liux.base.titlebar.TitleBar;
import com.liux.base.titlebar.TransparentTitleBar;
import com.liux.glide.GlideApp;
import com.liux.other.CountDownTimer;
import com.liux.player.listener.OnPlayerListener;
import com.liux.player.view.SurfacePlayerView;
import com.liux.player.view.TexturePlayerView;
import com.liux.view.SingleToast;

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

    private TexturePlayerView mTexturePlayerView;
    private SurfacePlayerView mSurfacePlayerView;

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

        mTexturePlayerView = (TexturePlayerView) findViewById(R.id.pv_player_texture);
        mTexturePlayerView.setOnPlayerListener(new OnPlayerListener() {
        });
        mSurfacePlayerView = (SurfacePlayerView) findViewById(R.id.pv_player_surface);
        mSurfacePlayerView.setOnPlayerListener(new OnPlayerListener() {
        });

        mBannerView = (BannerView) findViewById(R.id.vp_banner);

        mBanners = new ArrayList<String>();
        mBannerView.setScrollerTime(400);
        mBannerAdapter = new BannerAdapter<String>(mBanners, R.layout.item_banner) {
            @Override
            public void onBindData(BannerHolder holder, final String s, int index) {
                ImageView imageView = holder.getItemView();
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
                        SingleToast.makeText(ImmerseActivity.this, "点击了Banner:" + s, SingleToast.LENGTH_SHORT).show();
                    }
                });
            }
        };
        mBannerView.setAdapter(mBannerAdapter);
        mBannerView.setIndicator(new DefaultIndicator(this, R.drawable.indicator_bg));
    }

    @Override
    protected void onLazyLoad() {
        String[] paths = new String[20];
        for (int i = 0; i < paths.length; i++) {
            paths[i] = "http://lx0758.cc/templates/themes/default/static/img/rand/" + (i + 1) + ".jpg";
        }
        Observable.fromArray(paths)
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

        mSurfacePlayerView.setMedia("http://devimages.apple.com.edgekey.net/streaming/examples/bipbop_16x9/gear4/prog_index.m3u8");
//        mSurfacePlayerView.getPlayer().start();

        mTexturePlayerView.setMedia("http://devimages.apple.com.edgekey.net/streaming/examples/bipbop_16x9/gear4/prog_index.m3u8");
//        mTexturePlayerView.getPlayer().start();

//        GlideApp.with(this)
//                .asBitmap()
//                .load(VideoUrl.from("http://zhongjinshidai.zpftech.com/uploads/20170908/7d09ad02df3b8d031792ba7a8f0c3bcb.mp4"))
//                .into(mThumb);
    }

    @Override
    protected void onRestoreData(Map<String, Object> data) {

    }

    @Override
    protected void onSaveData(Map<String, Object> data) {

    }
}
