package com.liux.list.adapter;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

/**
 * Bean 和 View 绑定显示规则
 * @param <T>
 */
public abstract class Rule<T, VH extends RecyclerView.ViewHolder> {

    public int layout;

    /**
     * 规则绑定目标 Layout
     * @param layout
     */
    public Rule(@LayoutRes int layout) {
        this.layout = layout;
    }

    /**
     * 判断数据是否匹配规则
     * @param object
     * @return
     */
    public abstract boolean doBindData(Object object);

    /**
     * 创建ViewHolder
     * @param parent
     * @param layout
     * @return
     */
    public abstract VH createHolder(ViewGroup parent, int layout);

    /**
     * 绑定数据到View
     * @param holder
     * @param t
     * @param state
     * @param position
     */
    public abstract void onDataBind(VH holder, T t, State state, int position);
}
