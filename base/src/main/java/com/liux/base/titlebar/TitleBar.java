package com.liux.base.titlebar;

import android.content.Intent;
import android.os.Bundle;

/**
 * 自定义TitleBar需要实现此接口 <br>
 * 调用时机: <br>
 * 1.{@link com.liux.base.BaseActivity#onCreate(Bundle)} <br>
 * 2.{@link com.liux.base.BaseActivity#onInitTitleBar} to {@link TitleBar} <br>
 * 3.{@link com.liux.base.BaseActivity#onCreate(Bundle, Intent)} <br>
 * 4.{@link #initView} <br>
 * 5.{@link com.liux.base.BaseActivity#onTitleChanged(CharSequence, int)} <br>
 * 6.{@link #setTitle(String)}
 */
public interface TitleBar {

    void initView();

    void setTitle(String title);
}
