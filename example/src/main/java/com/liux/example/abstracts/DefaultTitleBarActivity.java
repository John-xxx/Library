package com.liux.example.abstracts;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.liux.abstracts.AbstractsActivity;
import com.liux.example.R;

/**
 * Created by Liux on 2017/12/3.
 */

public class DefaultTitleBarActivity extends AbstractsActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_demo);

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
}
