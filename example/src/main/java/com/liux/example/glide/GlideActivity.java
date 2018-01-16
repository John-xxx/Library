package com.liux.example.glide;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.liux.example.R;
import com.liux.glide.video.Video;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Liux on 2017/11/28.
 */

public class GlideActivity extends AppCompatActivity {
    private static final String TEST_URL = "https://lx0758.cc/html/files/big_buck_bunny_240p_h264.mp4";

    @BindView(R.id.iv_image)
    ImageView ivImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_glide);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_self, R.id.btn_realize})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_self:
                Glide.with(ivImage)
                        .asBitmap()
                        .load(TEST_URL)
                        .into(ivImage);
                break;
            case R.id.btn_realize:
                Glide.with(ivImage)
                        .asBitmap()
                        .load(new Video(TEST_URL))
                        .into(ivImage);
                break;
        }
    }
}
