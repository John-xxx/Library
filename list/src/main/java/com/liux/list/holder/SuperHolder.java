package com.liux.list.holder;

import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * 一个缓存全部条目控件的灵活的ViewHolder
 */
public class SuperHolder extends RecyclerView.ViewHolder {

    public static SuperHolder create(ViewGroup parent, @LayoutRes int layout) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new SuperHolder(view);
    }

    private SparseArray<View> mItemViews;

    public SuperHolder(View itemView) {
        super(itemView);
        mItemViews = new SparseArray<>();
    }

    public <T extends View>T getItemView() {
        return (T) itemView;
    }

    public <T extends View>T getView(@IdRes int id){
        View view = mItemViews.get(id);
        if(view == null){
            view = itemView.findViewById(id);
            mItemViews.put(id,view);
        }
        return (T) view;
    }

    public SuperHolder setText(@IdRes int id, String text) {
        ((TextView) getView(id)).setText(text);
        return this;
    }

    public SuperHolder setText(@IdRes int id, @StringRes int text) {
        ((TextView) getView(id)).setText(text);
        return this;
    }

    public SuperHolder setTextColor(@IdRes int id, int color) {
        ((TextView) getView(id)).setTextColor(color);
        return this;
    }

    public SuperHolder setBackgroundColor(@IdRes int id, int color) {
        getView(id).setBackgroundColor(color);
        return this;
    }

    public SuperHolder setVisibility(@IdRes int id, int visibility) {
        getView(id).setVisibility(visibility);
        return this;
    }

    public SuperHolder setOnClickListener(View.OnClickListener listener) {
        getItemView().setOnClickListener(listener);
        return this;
    }

    public SuperHolder setOnClickListener(@IdRes int id, View.OnClickListener listener) {
        getView(id).setOnClickListener(listener);
        return this;
    }

    public SuperHolder setOnLongClickListener(View.OnLongClickListener listener) {
        getItemView().setOnLongClickListener(listener);
        return this;
    }

    public SuperHolder setOnLongClickListener(@IdRes int id, View.OnLongClickListener listener) {
        getView(id).setOnLongClickListener(listener);
        return this;
    }
}