package com.liux.example.abstracts;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.WindowManager;

import com.liux.abstracts.AbstractsActivity;
import com.liux.abstracts.titlebar.TitleBar;
import com.liux.abstracts.titlebar.TransparentTitleBar;
import com.liux.example.R;

import java.util.Map;

/**
 * Created by Liux on 2017/12/3.
 */

public class TransparentTitleBarActivity extends AbstractsActivity {

    @Override
    protected TitleBar onInitTitleBar() {
        return new TransparentTitleBar(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState, Intent intent) {
        setContentView(R.layout.activity_base_demo);

        //WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING
        //WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
        //WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        //WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    @Override
    protected void onInitData(@Nullable Bundle savedInstanceState, Intent intent) {

    }

    @Override
    protected void onRestoreData(Map<String, Object> data) {

    }

    @Override
    protected void onInitView(@Nullable Bundle savedInstanceState) {

    }

    @Override
    protected void onInitViewFinish() {

    }

    @Override
    protected void onLazyLoad() {

    }

    @Override
    protected void onSaveData(Map<String, Object> data) {

    }
}
