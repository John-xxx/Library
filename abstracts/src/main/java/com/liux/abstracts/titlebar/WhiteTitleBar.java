package com.liux.abstracts.titlebar;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import com.liux.abstracts.R;

/**
 * 白色背景的 TitleBar 实现
 * Created by Liux on 2017/11/7.
 */

public class WhiteTitleBar extends DefaultTitleBar {

    public WhiteTitleBar(AppCompatActivity activity) {
        super(activity);
    }

    @Override
    public void initView() {
        super.initView();
        getBackIcon().setImageResource(R.drawable.view_titlebar_white_back);

        setStatusBarColor(Color.WHITE);
        setTitleBarColor(Color.WHITE);
        setTitleColor(Color.parseColor("#333333"));

        View view = new View(getActivity());
        view.setBackgroundColor(Color.parseColor("#EEEEEE"));

        float scale = getActivity().getResources().getDisplayMetrics().density;
        ((ViewGroup) getView()).addView(view, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                (int)(1.0F * scale + 0.5F)
        ));

        setStatusBarMode(true);
    }
}
