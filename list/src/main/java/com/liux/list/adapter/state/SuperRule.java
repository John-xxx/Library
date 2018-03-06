package com.liux.list.adapter.state;

import android.view.ViewGroup;

import com.liux.list.adapter.rule.Rule;
import com.liux.list.holder.SuperHolder;

/**
 * Created by Liux on 2017/11/30.
 * @param <T> 要处理的类型
 */

public abstract class SuperRule<T> extends Rule<T, SuperHolder> {

    public SuperRule(int layout) {
        super(layout);
    }

    @Override
    public SuperHolder createHolder(ViewGroup parent, int layout) {
        return SuperHolder.create(parent, layout);
    }
}
