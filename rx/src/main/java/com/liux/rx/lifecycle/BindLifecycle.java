package com.liux.rx.lifecycle;

import com.trello.rxlifecycle2.LifecycleTransformer;

/**
 * 2018/2/13
 * By Liux
 * lx0758@qq.com
 */

public interface BindLifecycle {

    <T> LifecycleTransformer<T> bindLifeCycle();
}