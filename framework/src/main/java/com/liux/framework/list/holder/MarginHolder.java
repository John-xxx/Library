package com.liux.framework.list.holder;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Liux on 2017/9/19.
 */

public class MarginHolder extends RecyclerView.ViewHolder {

    public static MarginHolder create(ViewGroup parent, @LayoutRes int layout) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new MarginHolder(view);
    }

    public MarginHolder(View itemView) {
        super(itemView);
    }
}
