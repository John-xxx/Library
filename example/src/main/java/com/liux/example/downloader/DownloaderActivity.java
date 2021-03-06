package com.liux.example.downloader;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.liux.downloader.Config;
import com.liux.downloader.Downloader;
import com.liux.example.R;

/**
 * Created by Liux on 2017/12/8.
 */

public class DownloaderActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloader);

        Config.Builder builder = new Config.Builder()
                .director(getExternalCacheDir())
                .maxDownloadCount(5);
        //Downloader.initialize(this);
        Downloader.initialize(this, builder.build());
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void oneTask(View view) {
        startActivity(new Intent(this, OneTaskActivity.class));
    }

    public void moreTask(View view) {
        startActivity(new Intent(this, MoreTaskActivity.class));
    }
}
