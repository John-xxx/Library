package com.liux.example.downloader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.liux.downloader.Config;
import com.liux.downloader.Downloader;
import com.liux.example.R;

/**
 * Created by Liux on 2017/12/8.
 */

public class DownloaderActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloader);

        Config.Builder builder = new Config.Builder()
                .director(getExternalCacheDir())
                .maxDownloadCount(5);
        Downloader.setConfig(builder.build());
        Downloader.initialize(this);
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
