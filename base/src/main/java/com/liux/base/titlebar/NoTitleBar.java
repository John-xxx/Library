package com.liux.base.titlebar;

import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;

/**
 * 没有Toolbar的状态栏,黑色状态栏背景
 * Created by Liux on 2017/11/7.
 */

public class NoTitleBar extends TitleBar {

    public NoTitleBar(AppCompatActivity activity) {
        super(activity);
    }

    @Override
    public void initView() {
        setStatusBarColor(Color.BLACK);
        getActivity().getSupportActionBar().hide();
    }

    @Override
    public void setTitle(String title) {

    }

    @Override
    public void setTitleColor(int color) {

    }

    public NoTitleBar setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().getWindow().setStatusBarColor(color);
        }
        return this;
    }
}
