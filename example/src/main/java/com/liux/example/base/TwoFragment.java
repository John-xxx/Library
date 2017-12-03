package com.liux.example.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.liux.abstracts.AbstractsFragment;
import com.liux.example.R;

/**
 * Created by Liux on 2017/12/3.
 */

public class TwoFragment extends AbstractsFragment {
    @Override
    protected void onInitData(Bundle savedInstanceState) {

    }

    @Override
    protected void onRestoreData(Bundle data) {

    }

    @Override
    protected View onInitView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_base_two, container, false);
        return rootView;
    }

    @Override
    protected void onLazyLoad() {

    }

    @Override
    protected void onSaveData(Bundle data) {

    }

    @Override
    protected void onVisibleChanged() {

    }
}
