package com.liux.example.abstracts;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.liux.abstracts.titlebar.NoTitleBar;
import com.liux.abstracts.titlebar.TitleBar;
import com.liux.example.R;

/**
 * Created by Liux on 2017/12/3.
 */

public class NoTitleBarActivity extends com.liux.abstracts.AbstractsActivity {
    @Override
    public TitleBar onInitTitleBar() {
        return new NoTitleBar(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_demo);
    }
}
