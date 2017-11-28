package com.liux.example.player;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.liux.example.R;

/**
 * Created by Liux on 2017/11/28.
 */

public class PlayerActivity extends AppCompatActivity {

    public static final String[] SOURCE = {
            "http://movie.ks.js.cn/flv/2011/11/8-1.flv",
            "rtmp://live.hkstv.hk.lxdns.com/live/hks",
            "rtsp://184.72.239.149/vod/mp4://BigBuckBunny_175k.mov",
            "http://live.hkstv.hk.lxdns.com/live/hks/playlist.m3u8"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
    }

    public void oneMedia(View view) {
        startActivity(new Intent(this, OneMediaActiviy.class));
    }

    public void moreMedia(View view) {
        startActivity(new Intent(this, MoreMediaActiviy.class));
    }
}
