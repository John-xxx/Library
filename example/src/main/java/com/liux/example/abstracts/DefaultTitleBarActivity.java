package com.liux.example.abstracts;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.liux.abstracts.AbstractsActivity;
import com.liux.example.R;

import java.util.Map;

/**
 * Created by Liux on 2017/12/3.
 */

public class DefaultTitleBarActivity extends AbstractsActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState, Intent intent) {
        setContentView(R.layout.activity_base_demo);
    }

    @Override
    protected void onInitData(@Nullable Bundle savedInstanceState, Intent intent) {

    }

    @Override
    protected void onRestoreData(Map<String, Object> data) {

    }

    @Override
    protected void onInitView(@Nullable Bundle savedInstanceState) {
//        DefaultTitleBar titleBar = getTitleBar();
//        titleBar
//                .setTitleBarColor()
//                .setStatusBarColor()
//                .setOnTitleBarListener()
//                .setStatusBarMode(false)
//                .setTitle()
//                .setTitleColor()
//                .hasBack()
//                .getBack()
//                .getBackIcon()
//                .getBackText()
//                .hasMore()
//                .getMore()
//                .getMoreIcon()
//                .getMoreText();
        // 忽略某控件
        addIgnoreView(findViewById(R.id.btn_button_1));
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
