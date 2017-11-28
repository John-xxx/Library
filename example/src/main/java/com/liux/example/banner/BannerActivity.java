package com.liux.example.banner;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.liux.banner.BannerAdapter;
import com.liux.banner.BannerHolder;
import com.liux.banner.BannerView;
import com.liux.banner.DefaultIndicator;
import com.liux.example.R;
import com.liux.glide.GlideApp;
import com.liux.view.SingleToast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Liux on 2017/11/28.
 */

public class BannerActivity extends AppCompatActivity {

    private List<String> mBanners;
    private BannerView mBannerView;
    private BannerAdapter<String> mBannerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_banner);

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
                        SingleToast.makeText(BannerActivity.this, "点击了Banner:" + s, SingleToast.LENGTH_SHORT).show();
                    }
                });
            }
        };
        mBannerView.setAdapter(mBannerAdapter);
        mBannerView.setIndicator(new DefaultIndicator(this, R.drawable.indicator_bg));
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);


        String[] paths = new String[20];
        for (int i = 0; i < paths.length; i++) {
            paths[i] = "http://lx0758.cc/templates/themes/default/static/img/rand/" + (i + 1) + ".jpg";
        }
        Observable.fromArray(paths)
                .delay(100, TimeUnit.MILLISECONDS)
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
}
